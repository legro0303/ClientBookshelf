package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.LoadSceneService;

public class StartController {

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
              AlertService alertService = new AlertService();
              alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "В данный момент сервер не функционирует");
            e.printStackTrace();
          }
            LoadSceneService loadSceneService = new LoadSceneService();
            loadSceneService.setScene(loader, startButton, "/FXML/authorization.fxml",
                    stage, "Авторизация");
        });
  }
}
