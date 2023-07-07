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
    ObservableList<User> modelLeaderboard = FXCollections.observableArrayList();
    private IServices service;
    private User loggedUser;

    @FXML
    Label usernameLabel;
    @FXML
    Label gameStatusLabel;
    @FXML
    Button logOutButton;
    @FXML
    ListView<User> leaderboardListView;
    @FXML
    Label leaderboardLabel;
    @FXML
    Label yourGameLabel;
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
    public void initVisuals() {

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


}
