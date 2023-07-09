package services;

import dto.ActionDTO;
import dto.GameDTO;
import dto.UpdateDTO;

public interface IObserver {
    void update(UpdateDTO updateDTO);

    void gameStarted(GameDTO gameDTO);

    void gameEndedLost(GameDTO gameDTO);

    void gameEndedWon(GameDTO data);

    void goStartScreen();

//    void gameStarted(PlayersDTO players) throws ServiceException;
//
//    void sendToWaitingRoom();
//
//    void roundFinished(RoundEndDTO round);
//
//    void gameFinished(String userStatus);
}
