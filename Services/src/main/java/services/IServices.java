package services;

import dto.UserMoveDTO;
import dto.WinnerDTO;
import model.Card;
import model.Deck;
import model.User;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    //Iterable<Trip> getAllTrips() throws ServiceException;
    //Iterable<Trip> getAllTripsFiltered(String objective,String dateStart,String dateEnd) throws ServiceException;
    //void addReservation(Reservation reservation) throws ServiceException;
    void logout(User user) throws ServiceException;

}
