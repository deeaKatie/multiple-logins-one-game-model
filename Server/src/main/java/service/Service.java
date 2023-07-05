package service;

import dto.PlayersDTO;
import exception.RepositoryException;
import model.User;
import repository.IUserRepository;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Service implements IServices {

    private IUserRepository userRepository;
    private Map<Long, IObserver> loggedClients; // key - id , val - observer
    private Map<Long, IObserver> playingClients; // key - id , val - observer
    private final int defaultThreadsNo = 5;

    public Service(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.loggedClients = new ConcurrentHashMap<>();
        this.playingClients = new ConcurrentHashMap<>();
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
            this.loggedClients.put(user.getId(), client);

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
        if (loggedClients.size() < 2) {
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

            // tell users game has started and give the players list
            for (var client : loggedClients.entrySet()) {
                //if (!Objects.equals(client.getKey(), user_id)) {
                    System.out.println("seding to: " + client.getKey());
                    client.getValue().gameStarted(players);
                //}
            }

            // clients are currently playing
            loggedClients.clear();
        }
    }


}
