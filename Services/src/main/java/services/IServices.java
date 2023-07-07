package services;

import model.User;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;

}
