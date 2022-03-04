package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartController {

  @FXML private ResourceBundle resources;

  @FXML private URL location;

  @FXML private Button startButton;

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();

  @FXML
  void initialize() {
    startButton.setOnAction(
        actionEvent -> {
          HttpResponse<String> req = null;
          try {
            req = Unirest.get("http://localhost:8080/message").asString();
          } catch (UnirestException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setContentText("В данный момент сервер не функционирует");
            alert.setHeaderText(null);
            alert.showAndWait();
          }
          startButton.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/authorization.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });
  }
}
