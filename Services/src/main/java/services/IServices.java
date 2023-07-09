package services;

import dto.ActionDTO;
import dto.ListItemsDTO;
import model.User;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;
    ListItemsDTO getData() throws ServiceException;
    void madeAction(ActionDTO action) throws ServiceException;

}
