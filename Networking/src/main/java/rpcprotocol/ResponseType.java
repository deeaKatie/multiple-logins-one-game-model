package rpcprotocol;

public enum ResponseType {
    OK,
    ERROR,
    GAME_STARTED,
    ROUND_END,
    GAME_FINISHED,
    G_FINISHED,
    WAITING_ROOM;
    private ResponseType() {
    }
}
