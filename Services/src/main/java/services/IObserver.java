package services;

import dto.ActionDTO;
import dto.UpdateDTO;

public interface IObserver {
    void update(UpdateDTO updateDTO);

//    void gameStarted(PlayersDTO players) throws ServiceException;
//
//    void sendToWaitingRoom();
//
//    void roundFinished(RoundEndDTO round);
//
//    void gameFinished(String userStatus);
}
