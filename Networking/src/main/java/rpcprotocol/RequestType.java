package rpcprotocol;

public enum RequestType {
    LOGIN,
    LOGOUT,
    SELECTED_CARD,
    START_GAME;
    private RequestType() {
    }
}
