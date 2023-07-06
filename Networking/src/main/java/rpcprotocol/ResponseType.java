package rpcprotocol;

public enum ResponseType {
    OK,
    ERROR,
    GAME_STARTED,
    ROUND_END,
    WAITING_ROOM;
    private ResponseType() {
    }
}
