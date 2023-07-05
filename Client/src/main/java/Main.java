import controller.LogInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import rpcprotocol.ServicesRpcProxy;
import services.IServices;

import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    private IServices service;
    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";
    public static void main(String[] args) {
        System.out.println("Hello world!");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initView(primaryStage);
        primaryStage.show();
    }

    private void initView(Stage primaryStage) throws IOException {
        System.out.println("In start");
        Properties clientProps = new Properties();

        try {
            clientProps.load(Main.class.getResourceAsStream("client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException var13) {
            System.err.println("Cannot find client.properties " + var13);
            return;
        }

        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException var12) {
            System.err.println("Wrong port number " + var12.getMessage());
            System.out.println("Using default port: " + defaultPort);
        }

        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);
        service = new ServicesRpcProxy(serverIP, serverPort);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LogInView.fxml"));

        AnchorPane appLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(appLayout));

        LogInController logInController = fxmlLoader.getController();
        logInController.setService(service);

        primaryStage.setTitle("Travel Agency");
    }
}