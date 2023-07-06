package service;

import dto.PlayersDTO;
import dto.RoundEndDTO;
import dto.UserMoveDTO;
import dto.WinnerDTO;
import exception.RepositoryException;
import model.Card;
import model.Deck;
import model.Game;
import model.User;
import repository.ICardDBRepository;
import repository.IGameDBRepository;
import repository.IUserRepository;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IServices {

    private IUserRepository userRepository;
    private ICardDBRepository cardDBRepository;
    private IGameDBRepository gameDBRepository;
    private Map<Long, IObserver> loggedClients; // key - id , val - observer
    private Map<Long, IObserver> playingClients; // key - id , val - observer

    private Map<Long, Card> currentRoundCards;
    private final int defaultThreadsNo = 5;
    // private Map.Entry<Long, IObserver> client;

    private Game game;

    public Service(IUserRepository userRepository, ICardDBRepository cardDBRepository, IGameDBRepository gameDBRepository) {
        this.userRepository = userRepository;
        this.cardDBRepository = cardDBRepository;
        this.gameDBRepository = gameDBRepository;
        this.loggedClients = new ConcurrentHashMap<>();
        this.playingClients = new ConcurrentHashMap<>();
        this.currentRoundCards = new ConcurrentHashMap<>();
        game = new Game();
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
        if (loggedClients.containsKey(user.getId())) {
            loggedClients.remove(user.getId());
        } else if (playingClients.containsKey(user.getId())) {
            noMoreCards(user);
            loggedClients.remove(user.getId());
        }
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
            this.game = game;
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

            while (noOfCards < 4) {
                noOfCards++;
                Random rand = new Random();
                int randomCardIndex = rand.nextInt(8);
                deck.addCard(cards.get(randomCardIndex));
            }

            game.addPlayer(user, deck);
        }

        return game;
    }

    @Override
    public void cardSelected(UserMoveDTO move) throws ServiceException {
        System.out.println("Card selected in srv");

        currentRoundCards.put(move.getPlayer().getId(), move.getSelectedCard());

        ArrayList<Card> playedCards = new ArrayList<>();
        Long roundWinner = null;
        Card winnerCard = null;

        for (var user_card : currentRoundCards.entrySet()) {
            playedCards.add(user_card.getValue());
            if (isCardBetter(user_card.getValue(), winnerCard)) {
                winnerCard = user_card.getValue();
                roundWinner = user_card.getKey();
            }
        }

        int noOfWinnerCards = 0;
        for (var user_card : currentRoundCards.entrySet()) {
            if (Objects.equals(user_card.getValue().getValue(), winnerCard.getValue())) {
                noOfWinnerCards++;
                System.out.println("card: " + user_card.getValue().getValue() + " nr: " + noOfWinnerCards);
            }
        }

        RoundEndDTO round = new RoundEndDTO(playedCards, roundWinner);

        // all players played cards
        if (currentRoundCards.size() == playingClients.size()) {

            for (var player : playingClients.entrySet()) {
                Long playerId = player.getKey();
                Card playedCard = currentRoundCards.get(playerId);
                round.setPlayedCard(playedCard);

                System.out.println("player id: " + playerId + " roundWinner: " + roundWinner);
                if (Objects.equals(playerId, roundWinner) && noOfWinnerCards == 1) {
                    System.out.println("TRUE - " + playerId + " - " + roundWinner);
                    round.setPlayerWon(true);
                } else {
                    System.out.println("FALSE - " + playerId + " - " + roundWinner);
                    round.setPlayerWon(false);
                }
                player.getValue().roundFinished(round);
            }

            //empty data
            currentRoundCards.clear();

        }
    }

    boolean isCardBetter(Card a, Card b) {
        Map<String, Integer> cardOrder = new HashMap<>();
        cardOrder.put("6", 0);
        cardOrder.put("7", 1);
        cardOrder.put("8", 2);
        cardOrder.put("9", 3);
        cardOrder.put("J", 4);
        cardOrder.put("Q", 5);
        cardOrder.put("K", 6);
        cardOrder.put("A", 7);

        if ((b == null) ||
                (cardOrder.get(a.getValue()) > cardOrder.get(b.getValue()))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean noMoreCards(User loggedUser) throws ServiceException {
        // remove from playing, put to waiting
        loggedClients.put(loggedUser.getId(), playingClients.get(loggedUser.getId()));
        var clientId = loggedUser.getId();
        playingClients.remove(loggedUser.getId());

        // if only one player, then game end
        if (playingClients.size() == 1) {

            // waiting room peeps
            System.out.println("LC");
            loggedClients.entrySet().forEach(System.out::println);
            for (var client : loggedClients.entrySet()) {
                if (client.getKey() != clientId) {
                    client.getValue().gameFinished("");
                }
            }

            // send winner, won message
            for (var client : playingClients.entrySet()) {
                client.getValue().gameFinished("won");
                loggedClients.put(client.getKey(), client.getValue());
            }

            playingClients.clear();

            System.out.println("SERVICE-> game finished");
            return true; // game finished
        }
        System.out.println("SERVICE-> game still going");
        return false; // game is still going go to waiting
    }

    @Override
    public void sendWinnerCards(WinnerDTO data) throws ServiceException {
        game.addWinner(data.getWinner(), data.getWinnerDeck());
        gameDBRepository.add(game);
        System.out.println("SERVICE -> Game saved to DB!\n");
    }

}
