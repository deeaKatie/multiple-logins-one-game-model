package controller;

import dto.PlayersDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.Card;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.util.ArrayList;


public class PlayController implements IObserver {
    ObservableList<User> modelPlayers = FXCollections.observableArrayList();
    ObservableList<Card> modelCards = FXCollections.observableArrayList();

    private IServices service;
    private User loggedUser;

    @FXML
    Label usernameLabel;
    @FXML
    Label gameStatusLabel;
    @FXML
    Button logOutButton;

    // PS
    @FXML
    ListView<User> playersListViewPS;
    @FXML
    ListView<Card> cardsListViewPS;
    @FXML
    Label playersLabelPS;
    @FXML
    Label cardsLabelPS;

    // SS
    @FXML
    Button startGameButtonSS;


    boolean sentToWaiting = false;

    public void setService(IServices service) {
        this.service = service;
    }
    public void setUser(User user) {
        this.loggedUser = user;
        usernameLabel.setText("Hi, " + loggedUser.getUsername());
    }
    public void init_PlayScreen(){

        startGameButtonSS.setVisible(false);
        playersListViewPS.setVisible(true);
        cardsListViewPS.setVisible(true);
        playersLabelPS.setVisible(true);
        cardsLabelPS.setVisible(true);
    }
    public void init_StartScreen(){
        if (!sentToWaiting) {
            startGameButtonSS.setVisible(true);
            playersLabelPS.setVisible(false);
            cardsLabelPS.setVisible(false);
            playersListViewPS.setVisible(false);
            cardsListViewPS.setVisible(false);
            gameStatusLabel.setVisible(false);
        }
    }
    public void init_WaitScreen(){
        playersListViewPS.setVisible(false);
        cardsListViewPS.setVisible(false);
        gameStatusLabel.setText("Waiting...");
        cardsLabelPS.setVisible(false);
        playersLabelPS.setVisible(false);
    }

    @FXML
    public void startGameHandler() {
        try {
            service.startGame(loggedUser.getId());
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Failed to start game", ex.getMessage());
        }
        System.out.println("Starting game!\n");
    }

    @FXML
    public void logOutHandler() {
        System.out.println("Logging out!\n");
        try {
            service.logout(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error logging out", ex.getMessage());
        }

    }

    @Override
    public void gameStarted(PlayersDTO players) {
        System.out.println("CONTROLLER -> 1gameStarted");
        Platform.runLater(()->{
            System.out.println("CONTROLLER -> 2gameStarted");
            // switch to game started view
            init_PlayScreen();

            modelPlayers.setAll(players.getPlayers());
            playersListViewPS.setItems(modelPlayers);

            modelCards.setAll(players.getDeck().getCards());
            cardsListViewPS.setItems(modelCards);
        });
    }


    public void sendToWaitingRoom() {
        sentToWaiting = true;
        init_WaitScreen();
    }
}
