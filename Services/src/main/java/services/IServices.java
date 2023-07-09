package services;

import dto.ActionDTO;
import dto.ListItemsDTO;
import dto.StartGameDTO;
import model.User;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    void logout(User user) throws ServiceException;
    ListItemsDTO getData(User user) throws ServiceException;
    void madeAction(ActionDTO action) throws ServiceException;
    Boolean startGame(StartGameDTO startGameDTO) throws ServiceException;
}
