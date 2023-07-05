package rpcprotocol;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType type;
    private Object data;

    private Request() {
    }

    public RequestType type() {
        return this.type;
    }

    public Object data() {
        return this.data;
    }

    public String toString() {
        return "Request{type='" + this.type + '\'' + ", data='" + this.data + '\'' + '}';
    }

    private void data(Object data) {
        this.data = data;
    }

    private void type(RequestType type) {
        this.type = type;
    }

    public static class Builder {
        private Request request = new Request();

        public Builder() {
        }

        public Builder type(RequestType type) {
            this.request.type(type);
            return this;
        }

        public Builder data(Object data) {
            this.request.data(data);
            return this;
        }

        public Request build() {
            return this.request;
        }
    }
}
