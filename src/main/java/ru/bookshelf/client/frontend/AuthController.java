package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.bookshelf.client.domain.entity.Person;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController {
  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();
  @FXML private ResourceBundle resources;

  @FXML private URL location;

  @FXML private PasswordField passAuth;

  @FXML private TextField loginAuth;

  @FXML private Button buttonAuth;

  @FXML private Hyperlink linkAuth;

  @FXML
  void initialize() {

    linkAuth.setOnAction(
        actionEvent -> {
          linkAuth.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/registration.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });

    buttonAuth.setOnAction(
        actionEvent -> {
          Person person = new Person();
          JsonNode validationResult = new JsonNode(null);
          Boolean userAuthorization;
          person.setLogin(loginAuth.getText());
          person.setPassword(passAuth.getText());
          try {
            validationResult =
                Unirest.post("http://localhost:8080/message/authorization")
                    .header("accept", "application/json")
                    .field("login", person.getLogin())
                    .field("password", person.getPassword())
                    .asJson()
                    .getBody();

          } catch (UnirestException e) {
            e.printStackTrace();
          }
          userAuthorization = (Boolean) validationResult.getObject().get("authorization");
          if (userAuthorization == true) {
            buttonAuth.getScene().getWindow().hide();
            loader.setLocation(getClass().getResource("/FXML/mainMenu.fxml"));

            try {
              loader.load();
            } catch (IOException e) {
              e.printStackTrace();
            }

            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
            stage.show();

          } else if (userAuthorization == false) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setContentText(
                "Вы ввели неверный логин или пароль, пожалуйста, попробуйте ввести данные ещё раз.");
            alert.setHeaderText(null);
            alert.showAndWait();
          }
          // }
        });
  }
}
