package controller;

import dto.PlayersDTO;
import dto.RoundEndDTO;
import dto.UserMoveDTO;
import dto.WinnerDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Card;
import model.Deck;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.io.IOException;
import java.util.ArrayList;
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
    public void logOutHandler() throws IOException {
        System.out.println("Logging out!\n");
        try {
            service.logout(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error logging out", ex.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LogInView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = (Stage) logOutButton.getScene().getWindow();
        LogInController logCtrl = fxmlLoader.getController();

        logCtrl.setService(service);
        stage.setScene(scene);
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

    @Override
    public void gameFinished(String userStatus) {
        Platform.runLater(() -> {
            sentToWaiting = false;
            init_StartScreen();
            System.out.println("CTRL -> Received status: " + userStatus);
            if (Objects.equals(userStatus, "won")) {
                gameStatusLabel.setText("You won the game!");
                gameStatusLabel.setVisible(true);

                Deck deck = new Deck();
                for(var c : modelCards) {
                    deck.addCard(c);
                }
                try {
                    System.out.println("CTRL -> Send winner cards");
                    WinnerDTO data = new WinnerDTO(loggedUser, deck);
                    service.sendWinnerCards(data);
                } catch (Exception e) {
                    MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error logging out", e.getMessage());
                }
            }
        });

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
                    boolean status = service.noMoreCards(loggedUser);
                    if (status) { // game ended
                        gameFinished("");
                    } else { // game still going go to waiting room
                        sendToWaitingRoom();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
