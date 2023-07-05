package rpcprotocol;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private Object data;

    private Response() {
    }

    public ResponseType type() {
        return this.type;
    }

    public Object data() {
        return this.data;
    }

    private void type(ResponseType type) {
        this.type = type;
    }

    private void data(Object data) {
        this.data = data;
    }

    public String toString() {
        return "Response{type='" + this.type + '\'' + ", data='" + this.data + '\'' + '}';
    }

    public static class Builder {
        private Response response = new Response();

        public Builder() {
        }

        public Builder type(ResponseType type) {
            this.response.type(type);
            return this;
        }

        public Builder data(Object data) {
            this.response.data(data);
            return this;
        }

        public Response build() {
            return this.response;
        }
    }
}
