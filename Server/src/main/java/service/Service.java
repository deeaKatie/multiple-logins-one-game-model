package service;

import dto.*;
import exception.RepositoryException;
import model.Game;
import model.User;
import repository.IGameDBRepository;
import repository.IUserRepository;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IServices {

    private IUserRepository userRepository;
    private IGameDBRepository gameDBRepository;
    private Map<Long, IObserver> loggedClients; // all logged clients
    private Map<Long, IObserver> playingClients;
    private Game onGoingGame; // only one game at a time
    private int noOfPlayersInAGame;
    private final int defaultThreadsNo = 5;

    public Service(IUserRepository userRepository, IGameDBRepository gameDBRepository){
        this.userRepository = userRepository;
        this.gameDBRepository = gameDBRepository;
        this.loggedClients = new ConcurrentHashMap<>();
        this.playingClients = new ConcurrentHashMap<>();
        noOfPlayersInAGame = 2;
        onGoingGame = new Game();
    }

    public synchronized User checkLogIn(User user, IObserver client) throws ServiceException {
        //find user
        User userToFind;
        System.out.println("SERVER -> checkLogIn -> " + user);
        try {
            userToFind = userRepository.findUserByUsername(user.getUsername());
        } catch (RepositoryException re) {
            throw new ServiceException(re.getMessage());
        }
        // check if user is already logged in
        if (loggedClients.containsKey(userToFind.getId())) {
            throw new ServiceException("User already logged in.");
        }
        // check if password is correct
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
        System.out.println("SERVER -> logout");

        //todo during game
        if (loggedClients.containsKey(user.getId())) {
            loggedClients.remove(user.getId());
        } else{
            throw new ServiceException("User not logged in");
        }

    }

    @Override
    public Boolean startGame(StartGameDTO startGameDTO) throws ServiceException {
        System.out.println("SERVER -> startGame");

        // if game not already going
        // if we have enough waiting clients to start
        System.out.println("SERVER -> waitingClients.size() = " + loggedClients.size());
        if (playingClients.isEmpty() && loggedClients.size() >= noOfPlayersInAGame - 1) { // we have enough to start a match
            System.out.println("SERVER -> We have players to start a match");

            // get first noOfPlayersInAGame clients
            List<Long> clientsForThisGame = new ArrayList<>();
            for (var client : loggedClients.entrySet()) {
                clientsForThisGame.add(client.getKey());
            }

            // create game
            Game game = new Game();
            game = gameDBRepository.add(game);
            onGoingGame = game;

            // add clients to game & remove them from waiting list
            game.addPlayer(startGameDTO.getUser());
            for (var clientId : clientsForThisGame) {
                try {
                    game.addPlayer(userRepository.findById(clientId));
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
                playingClients.put(clientId, loggedClients.get(clientId));
            }

            // notify clients
            ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
            for (var client : game.getPlayers()) {
                executor.execute(() -> {
                    try {
                        System.out.println("SERVER -> Notifying clients mathc has started");
                        loggedClients.get(client.getId()).gameStarted(new GameDTO());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            return true;

        }

        // we don't have enough clients to start a match
        return false;

    }

    // gets data from repository in list form
    public synchronized Iterable<User> getListData() throws ServiceException {
        return userRepository.getAll();
    }

    // encapsulates data in DTO
    public synchronized ListItemsDTO getData(User user) throws ServiceException {
        System.out.println("SERVER -> getData");
        ListItemsDTO listItemsDTO = new ListItemsDTO();
        for (var item : getListData()) {
            ListItemDTO listItemDTO = new ListItemDTO();
            listItemDTO.setUser(item);
            listItemsDTO.addItem(listItemDTO);
        }
        return listItemsDTO;
    }

    public synchronized void madeAction(ActionDTO action) throws ServiceException {
        System.out.println("SERVER -> madeAction");

        // Do smth for action

        // Update other users
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (var client : loggedClients.entrySet()) {
            System.out.println("SERVER -> madeAction -> client: " + client);

            executor.execute(() -> {
                try {
                    UpdateDTO updateDTO = new UpdateDTO();
                    updateDTO.setEntities(getData(userRepository.findById(client.getKey())));
                    client.getValue().update(updateDTO);
                } catch (ServiceException | RepositoryException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    public synchronized void endGame(GameDTO gameDTO, Map<Long, Boolean> usersStatus) throws ServiceException {
        //Map<Long, Boolean> usersSTatus -> id, status 1 - winner, 0 - loser
        System.out.println("SERVER -> endGame");

        // remove players from playingClients
        for (var player : usersStatus.entrySet()) {
            playingClients.remove(player.getKey());
        }

        // notify players
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (var player : usersStatus.entrySet()) {
            executor.execute(() -> {
                try {
                    if (player.getValue()) {
                        // WINNER
                        loggedClients.get(player.getKey()).gameEndedWon(gameDTO);
                    } else {
                        // LOSER
                        loggedClients.get(player.getKey()).gameEndedLost(gameDTO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        //notify all logged clients
        for (var player : loggedClients.entrySet()) {
            executor.execute(() -> {
                try {
                    //notify just waiting room
                    //if (!playingClients.containsKey(player.getKey())) {
                        // Start game
                        player.getValue().goStartScreen();
                    //}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }




}
