package controller;

import dto.PlayersDTO;
import dto.RoundEndDTO;
import dto.UserMoveDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.Card;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.util.Objects;


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


    public void sendSelectedCard(Card card) {
        try {
            UserMoveDTO move = new UserMoveDTO(loggedUser, card);
            service.cardSelected(move);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleCardSelection(MouseEvent mouseEvent) {
        Card card = cardsListViewPS.getSelectionModel().getSelectedItem();
        sendSelectedCard(card);
        cardsListViewPS.setDisable(true);
    }

    @Override
    public void roundFinished(RoundEndDTO round) {
        System.out.println("Round finished Controller");

        Platform.runLater(()-> {
            cardsListViewPS.setDisable(false);
            gameStatusLabel.setVisible(true);

            // add all cards if round winner
            if (!round.getPlayerWon()) {
                gameStatusLabel.setText("You Lost");
                // remove card
                for (var c : modelCards) {
                    if (Objects.equals(c.getId(), round.getPlayedCard().getId())) {
                        System.out.println("controller remove card: " + c.getValue());
                        modelCards.remove(c);
                        break;
                    }
                }
            } else {
                String cards = "";
                for (var card : round.getRoundSelectedCards()) {
                    if (card.getValue() != round.getPlayedCard().getValue()) {
                        cards += card.getValue() + " | ";
                        modelCards.add(card);
                    }
                }
                gameStatusLabel.setText("You Won: " + cards);

            }
            cardsListViewPS.setItems(modelCards);

            // ran out of cards
            if (modelCards.isEmpty()) {
                try {
                    service.noMoreCards(loggedUser);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
