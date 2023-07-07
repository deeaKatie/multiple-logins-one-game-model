import model.User;
import repository.*;
import service.Service;
import services.IServices;
import utils.AbstractServer;
import utils.RpcConcurrentServer;
import utils.ServerException;

import java.io.IOException;
import java.util.Properties;

public class StartServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {


        //todo get rid of this if you want
        Properties serverProps=new Properties();
        try {
            serverProps.load(StartServer.class.getResourceAsStream("server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException var21) {
            System.err.println("Cannot find server.properties " + var21);
            return;
        }

        IUserRepository userRepository = new UserDBRepository();
        IGameDBRepository gameDBRepository = new GameDBRepository();

        IServices service=new Service(userRepository, gameDBRepository);

        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong  Port Number" + ex.getMessage());
            System.err.println("Using default port " + defaultPort);
        }

        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try {
            server.start();
        } catch (ServerException ex) {
            System.err.println("Error starting the server" + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException ex) {
                System.err.println("Error stopping server " + ex.getMessage());
            }

        }
    }
}
