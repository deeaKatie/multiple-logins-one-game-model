package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import services.IServices;
import services.ServiceException;
import utils.MessageAlert;
import java.io.IOException;

public class LogInController {
    @FXML
    private Button buttonLogIn;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldPassword;

    private IServices service;

    public void setService(IServices service) {
        this.service = service;
    }

    public void handleLogIn(ActionEvent actionEvent) throws IOException {
        String name = textFieldUsername.getText();
        String password = textFieldPassword.getText();

        try{
            User user = new User(name,password);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) buttonLogIn.getScene().getWindow();
            PlayController playCtrl = fxmlLoader.getController();

            user = service.checkLogIn(user, playCtrl);
            System.out.println("USER: " + user);

            playCtrl.setService(service);
            playCtrl.setUser(user);
            playCtrl.init_StartScreen();
            stage.setScene(scene);

        }catch (ServiceException re){
            System.out.println(re);
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Failed Login",re.getMessage());

        }
    }

}
