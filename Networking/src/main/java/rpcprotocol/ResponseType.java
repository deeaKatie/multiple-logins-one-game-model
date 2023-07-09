package rpcprotocol;

public enum ResponseType {
    OK,
    ERROR,
    UPDATE_DATA,
    GAME_STARTED,
    GO_WAITING,
    GAME_END_WIN,
    GAME_END_LOSE,
    GO_START_SCREEN;
    private ResponseType() {
    }
}
