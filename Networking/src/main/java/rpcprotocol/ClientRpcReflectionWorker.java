package rpcprotocol;

import dto.PlayersDTO;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class ClientRpcReflectionWorker implements Runnable, IObserver {
    private IServices service;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Response okResponse = (new Response.Builder().type(ResponseType.OK)).build();

    public ClientRpcReflectionWorker(IServices service, Socket connection) {
        this.service = service;
        this.connection = connection;

        try {
            this.output = new ObjectOutputStream(connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(connection.getInputStream());
            this.connected = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (this.connected) {
            try {
                Object request = this.input.readObject();
                System.out.println(request);
                Response response = this.handleRequest((Request) request);
                System.out.println(response);
                if (response != null) {
                    this.sendResponse(response);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
        } catch (IOException ex) {
            System.out.println("Error " + ex);
        }
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        this.output.writeObject(response);
        this.output.flush();
    }

    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + request.type();
        System.out.println("HandlerName " + handlerName);

        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            System.out.println("Method " + handlerName + " invoked");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return response;
    }


    private Response handleLOGIN(Request request) {
        System.out.println("Login request ..." + request.type());
        User user = (User)request.data();

        try {
            User newUser = this.service.checkLogIn(user, this);
            return (new Response.Builder()).type(ResponseType.OK).data(newUser).build();
        } catch (ServiceException ex) {
            this.connected = false;
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        System.out.println("Logout request...");
        User user = (User)request.data();

        try {
            this.service.logout(user);
            this.connected = false;
            return okResponse;
        } catch (ServiceException ex) {
            return (new Response.Builder()).type(ResponseType.ERROR).data(ex.getMessage()).build();
        }
    }

    private Response handleSTART_GAME(Request request) {
        System.out.println("WORKER -> handleSTART_GAME");
        System.out.println("Start game Request...");
        try {
            service.startGame((long) request.data());
            System.out.println("WORKER -> sending ok response");
            return okResponse;
        } catch (ServiceException ex) {
            return (new Response.Builder().type(ResponseType.ERROR)).data(ex.getMessage()).build();
        }
    }

    @Override
    public void gameStarted(PlayersDTO players) throws ServiceException {
        System.out.println("WORKER -> gameStarted");
        Response response = (new Response.Builder()).type(ResponseType.GAME_STARTED).data(players).build();
        try {
            sendResponse(response);
        } catch (Exception ex) {
            System.out.println("Error trying to send game staretd response");
        }
    }
}
