package service;

import dto.ActionDTO;
import dto.UpdateDTO;
import exception.RepositoryException;
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
    private Map<Long, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public Service(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.loggedClients = new ConcurrentHashMap<>();
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
        if (loggedClients.containsKey(user.getId())) {
            loggedClients.remove(user.getId());
        } else{
            throw new ServiceException("User not logged in");
        }
    }

    public synchronized Iterable<?> getListData() throws ServiceException {
        return userRepository.getAll();
    }

    public synchronized void madeAction(ActionDTO action) {
        System.out.println("SERVER -> madeAction");
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for (IObserver client : loggedClients.values()) {
            System.out.println("SERVER -> madeAction -> client: " + client);
            executor.execute(() -> {
                try {
                    UpdateDTO updateDTO = new UpdateDTO();
                    updateDTO.setEntities(getListData());
                    client.update(updateDTO);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            });
        }
    }


}
