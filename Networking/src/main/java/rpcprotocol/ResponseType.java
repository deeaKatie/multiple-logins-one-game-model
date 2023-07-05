package rpcprotocol;

public enum ResponseType {
    OK,
    ERROR,
    GAME_STARTED,
    WAITING_ROOM;
    private ResponseType() {
    }
}
