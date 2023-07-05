package services;

import dto.PlayersDTO;

public interface IObserver {
    //void reservationMade(Reservation reservation) throws ServiceException;
    void gameStarted(PlayersDTO players) throws ServiceException;
}
