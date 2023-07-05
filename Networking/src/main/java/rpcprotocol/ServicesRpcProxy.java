package rpcprotocol;

import dto.PlayersDTO;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;
    private IObserver client;
    private User loggedUser;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue<>();
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    @Override
    public User checkLogIn(User user, IObserver client) throws ServiceException {
        this.initializeConnection();
        Request req = (new Request.Builder()).type(RequestType.LOGIN).data(user).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            User foundUser = (User)response.data();
            user.setId(foundUser.getId());
            loggedUser = foundUser;
            return foundUser;
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            this.closeConnection();
            throw new ServiceException(err);
        }
        return null;
    }

    @Override
    public void logout(User user) throws ServiceException {
        Request req = (new Request.Builder()).type(RequestType.LOGOUT).data(user).build();
        this.sendRequest(req);
        Response response = this.readResponse();
        this.closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }

    @Override
    public void startGame(Long user_id) throws ServiceException {
        System.out.println("PROXY -> startGame");
        Request req =(new Request.Builder()).type(RequestType.START_GAME).data(user_id).build();
        sendRequest(req);
        System.out.println("PROXY -> trying to read response");
        Response response = readResponse();
        System.out.println("PROXY -> read response");

        if (response.type() == ResponseType.ERROR) {
            throw new ServiceException(response.data().toString());
        } else if (response.type() == ResponseType.OK) {
            System.out.println("PROXY -> OK REASPONSE");
            //client.gameStarted((PlayersDTO) response.data());
        }

        System.out.println("PROXY -> i suck");
    }

    private void initializeConnection() throws ServiceException {
        try {
            this.connection = new Socket(this.host, this.port);
            this.output = new ObjectOutputStream(this.connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(this.connection.getInputStream());
            this.finished = false;
            this.startReader();
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
    private void closeConnection() {
        this.finished = true;

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
            this.client = null;
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }
    private void handleUpdate(Response response) {
        System.out.println("PROXY -> handleUpdate");
        System.out.println("RESPONSE -> " + response);
        if (response.type() == ResponseType.GAME_STARTED) {
            try {
                client.gameStarted((PlayersDTO) response.data());
            } catch (ServiceException ex) {
                ex.printStackTrace();
            }
        }
    }
    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.GAME_STARTED;
    }
    private void sendRequest(Request request) throws ServiceException {
        try {
            this.output.writeObject(request);
            this.output.flush();
        } catch (IOException ex) {
            throw new ServiceException("Error sending object " + ex);
        }
    }

    private Response readResponse() throws ServiceException {
        Response response = null;
        try {
            response = (Response)this.qresponses.take();
            System.out.println("d");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    private class ReaderThread implements Runnable {
        private ReaderThread() {
        }

        public void run() {
            while(!ServicesRpcProxy.this.finished) {
                try {
                    Object response;
                    synchronized (input){
                        response = ServicesRpcProxy.this.input.readObject();
                    }

                    System.out.println("response received " + response);
                    if (ServicesRpcProxy.this.isUpdate((Response)response)) {
                        ServicesRpcProxy.this.handleUpdate((Response)response);
                    } else {
                        try {
                            ServicesRpcProxy.this.qresponses.put((Response)response);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }
                } catch (IOException var4) {
                    System.out.println("Reading error " + var4);
                } catch (ClassNotFoundException var5) {
                    System.out.println("Reading error " + var5);
                }
            }

        }
    }
}
