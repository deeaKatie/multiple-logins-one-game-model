package services;

import dto.PlayersDTO;
import dto.RoundEndDTO;

public interface IObserver {
    //void reservationMade(Reservation reservation) throws ServiceException;
    void gameStarted(PlayersDTO players) throws ServiceException;

    void sendToWaitingRoom();

    void roundFinished(RoundEndDTO round);

    void gameFinished(String userStatus);


}
