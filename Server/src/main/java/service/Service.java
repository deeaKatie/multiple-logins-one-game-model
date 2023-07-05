package service;

import dto.PlayersDTO;
import exception.RepositoryException;
import model.Card;
import model.Deck;
import model.Game;
import model.User;
import repository.ICardDBRepository;
import repository.IUserRepository;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IServices {

    private IUserRepository userRepository;
    private ICardDBRepository cardDBRepository;
    private Map<Long, IObserver> loggedClients; // key - id , val - observer
    private Map<Long, IObserver> playingClients; // key - id , val - observer
    private final int defaultThreadsNo = 5;
   // private Map.Entry<Long, IObserver> client;

    public Service(IUserRepository userRepository, ICardDBRepository cardDBRepository) {
        this.userRepository = userRepository;
        this.cardDBRepository = cardDBRepository;
        this.loggedClients = new ConcurrentHashMap<>();
        this.playingClients = new ConcurrentHashMap<>();
    }

    @Override
    public String checkGameState() {
        if (!playingClients.isEmpty())
            return "playing";
        return "waiting";
    }

    public synchronized User checkLogIn(User user, IObserver client) throws ServiceException {
        User userToFind;
        System.out.println("USER CHECKLLOG IN: " + user);
        try {
            System.out.println("+++++++++++++++ B4 repo!\n");
            userToFind = userRepository.findUserByUsername(user.getUsername());

        } catch (RepositoryException re) {
            throw new ServiceException(re.getMessage());
        }
        if (loggedClients.containsKey(userToFind.getId())) {
            throw new ServiceException("User already logged in.");
        }
        if (Objects.equals(userToFind.getPassword(), user.getPassword())) {
            user.setId(userToFind.getId());
            loggedClients.put(user.getId(), client);
            return userToFind;
        } else {
            throw new ServiceException("Incorrect Password");
        }
    }

    @Override
    public synchronized void logout(User user) throws ServiceException {
        loggedClients.remove(user.getId());
    }

    @Override
    public void startGame(Long user_id) throws ServiceException {
        System.out.println("SERVICE -> startGame");

        if (loggedClients.size() < 2 && playingClients.isEmpty()) {
            throw new ServiceException("Not enough Players!\n");
        } else {

            // get all currently logged users that will become players
            PlayersDTO players = new PlayersDTO();
            for (var client : loggedClients.entrySet()) {

                playingClients.put(client.getKey(), client.getValue());
                try {
                    players.addPlayer(userRepository.findById(client.getKey()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // send game data to players
            Game game = createGame();
            for (var player_deck : game.getPlayers().entrySet()) {
                Long playerId = player_deck.getKey().getId();
                var client = loggedClients.get(playerId);
                players.setDeck(player_deck.getValue());
                client.gameStarted(players);

            }
            loggedClients.clear();
        }
    }


    Game createGame() {
        Game game = new Game();

        ArrayList<Card> cards = (ArrayList<Card>) cardDBRepository.getAll();

        for (var client : playingClients.entrySet()) {
            User user;
            try {
                user = userRepository.findById(client.getKey());
            } catch (RepositoryException re) {
                re.printStackTrace();
                return null;
            }

            Deck deck = new Deck();

            int noOfCards = 0;

            while(noOfCards < 4) {
                noOfCards++;
                Random rand = new Random();
                int randomCardIndex = rand.nextInt(8);
                deck.addCard(cards.get(randomCardIndex));
            }

            game.addPlayer(user, deck);
        }

        return game;
    }

}
