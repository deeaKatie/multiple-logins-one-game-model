package controller;

import dto.ListItemDTO;
import dto.ListItemsDTO;
import dto.UpdateDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.User;
import services.IObserver;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;

import java.io.IOException;


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
            items = service.getData();
        } catch (ServiceException ex) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error getting data", ex.getMessage());
        }
        initModel(items);
    }

    public void initModel(ListItemsDTO items) {
        modelLeft.setAll(items.getItems());
        leftListView.setItems(modelLeft);
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
    public void update(UpdateDTO updateDTO) {
        initModel(updateDTO.getEntities());
    }
}
