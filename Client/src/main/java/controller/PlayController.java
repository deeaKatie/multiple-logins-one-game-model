package controller;

import dto.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;


public class PlayController implements IObserver {
    ObservableList<ListItemDTO> modelLeft = FXCollections.observableArrayList();
    private IServices service;
    private User loggedUser;
    @FXML
    Label usernameLabel;
    @FXML
    Label statusLabel;
    @FXML
    Button logOutButton;
    @FXML
    ListView<ListItemDTO> leftListView;
    @FXML
    Label leftLabel;
    @FXML
    Label rightLabel;
    @FXML
    Button makeActionButton;
    @FXML
    Button startGameButton;
    @FXML
    TextField gameInitDataTextField;

    public void setService(IServices service) {
        this.service = service;
    }

    public void setUser(User user) {
        this.loggedUser = user;
    }

    public void initVisuals() {
        usernameLabel.setText("Hi, " + loggedUser.getUsername());
        statusLabel.setVisible(false);
        ListItemsDTO items = null;
        try {
            items = service.getData(loggedUser);
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error getting data", ex.getMessage());
        }
        initModel(items);
    }

    public void initModel(ListItemsDTO items) {
        modelLeft.setAll(items.getItems());
        leftListView.setItems(modelLeft);
    }

    public void init_WaitScreen() {
        statusLabel.setVisible(true);
        statusLabel.setText("Waiting for other players...");
        leftListView.setVisible(false);
        leftLabel.setVisible(false);
        rightLabel.setVisible(false);
        makeActionButton.setVisible(false);
        gameInitDataTextField.setVisible(false);
        startGameButton.setVisible(false);
    }

    public void init_PlayScreen() {
        initVisuals();
        statusLabel.setVisible(false);
        leftListView.setVisible(true);
        leftLabel.setVisible(true);
        rightLabel.setVisible(true);
        gameInitDataTextField.setVisible(false);
        startGameButton.setVisible(false);
        makeActionButton.setVisible(true);
    }

    public void init_StartScreen() {
        statusLabel.setVisible(true);
        statusLabel.setText("Click Button to Start!");
        leftListView.setVisible(false);
        leftLabel.setVisible(false);
        rightLabel.setVisible(false);
        gameInitDataTextField.setVisible(true);
        startGameButton.setVisible(true);
        makeActionButton.setVisible(false);
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
    public void goStartScreen() {
        Platform.runLater(() -> {
            init_StartScreen();
        });
    }

    @Override
    public void update(UpdateDTO updateDTO) {
        initModel(updateDTO.getEntities());
    }

    public void makeAction(ActionEvent actionEvent) {
        try {
            service.madeAction(new ActionDTO());
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error making action", e.getMessage());
        }
    }

    public void startGame(ActionEvent actionEvent) {

        if (!gameInitDataTextField.getText().isEmpty()) {
            try {

                StartGameDTO startGameDTO = new StartGameDTO();
                startGameDTO.setUser(loggedUser);
                startGameDTO.setData(gameInitDataTextField.getText());

                Boolean status = service.startGame(startGameDTO);
                if (!status) {
                    init_WaitScreen();
                }

            } catch (ServiceException e) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error starting game", e.getMessage());
            }

        }
    }

    @Override
    public void gameStarted(GameDTO gameDTO) {
        Platform.runLater(() -> {
            init_PlayScreen();
        });

        // add any other data from gameDTO to the visuals
    }

    @Override
    public void gameEndedLost(GameDTO gameDTO) {

        // Do game ended stuff

        Platform.runLater(() -> {
            init_StartScreen();
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Return to start screen after 1 second
                    init_StartScreen();
                }
            }, 1000);
        });
    }

    @Override
    public void gameEndedWon(GameDTO data) {
        // Do game ended stuff

        Platform.runLater(() -> {
            init_StartScreen();
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // Return to start screen after 1 second
                    init_StartScreen();
                }
            }, 1000);
        });
    }
}
