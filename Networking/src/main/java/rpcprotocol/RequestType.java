package rpcprotocol;

public enum RequestType {
    LOGIN,
    LOGOUT,
    GET_DATA,
    MADE_ACTION;
    private RequestType() {
    }
}
