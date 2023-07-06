package services;

import dto.UserMoveDTO;
import model.Card;
import model.Deck;
import model.User;

public interface IServices {
    User checkLogIn(User user,IObserver client) throws ServiceException;
    //Iterable<Trip> getAllTrips() throws ServiceException;
    //Iterable<Trip> getAllTripsFiltered(String objective,String dateStart,String dateEnd) throws ServiceException;
    //void addReservation(Reservation reservation) throws ServiceException;
    void logout(User user) throws ServiceException;

    void startGame(Long user_id) throws ServiceException;

    String checkGameState();

    void cardSelected(UserMoveDTO move) throws ServiceException;

    boolean noMoreCards(User loggedUser) throws ServiceException;

    void sendWinnerCards(Deck deck) throws ServiceException;
}
