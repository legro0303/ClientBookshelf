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
import java.util.Optional;
import java.util.ResourceBundle;

public class RegController {

  @FXML private ResourceBundle resources;

  @FXML private URL location;

  @FXML private PasswordField passReg;

  @FXML private TextField loginReg;

  @FXML private Button buttonReg;

  @FXML private Button backToAuthButtonReg;

  @FXML private TextField secondNameReg;

  @FXML private TextField nameReg;

  @FXML private TextField mailReg;

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();

  @FXML
  void initialize() {
    backToAuthButtonReg.setOnAction(
        actionEvent -> {
          backToAuthButtonReg.getScene().getWindow().hide();
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

    buttonReg.setOnAction(
        actionEvent -> {
          if (passReg.getText().trim().isEmpty()
              || loginReg.getText().trim().isEmpty()
              || secondNameReg.getText().trim().isEmpty()
              || nameReg.getText().trim().isEmpty()
              || mailReg.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setContentText("Вы заполнили не все поля");
            alert.setHeaderText(null);
            alert.showAndWait();
          } else {
            Person person = new Person();
            person.setS_name(secondNameReg.getText());
            person.setF_name(nameReg.getText());
            person.setLogin(loginReg.getText());
            person.setMail(mailReg.getText());
            person.setPassword(passReg.getText());
            JsonNode validationResult = new JsonNode(null);
            Boolean validationLogin;
            try {
              validationResult =
                  Unirest.post("http://localhost:8080/message/validation")
                      .header("accept", "application/json")
                      .field("s_name", person.getS_name())
                      .field("f_name", person.getF_name())
                      .field("login", person.getLogin())
                      .field("mail", person.getMail())
                      .field("password", person.getPassword())
                      .asJson()
                      .getBody();

            } catch (UnirestException e) {
              e.printStackTrace();
            }
            validationLogin = (Boolean) validationResult.getObject().get("validationLogin");
            if (validationLogin == true) {
              try {
                Unirest.post("http://localhost:8080/message/registration")
                    .header("accept", "application/json")
                    .field("s_name", person.getS_name())
                    .field("f_name", person.getF_name())
                    .field("login", person.getLogin())
                    .field("mail", person.getMail())
                    .field("password", person.getPassword())
                    .asJson();
              } catch (UnirestException e) {
                e.printStackTrace();
              }
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Вы зарегистрированы");
              alert.setContentText("Успех! Вы зарегистрированы");
              alert.setHeaderText(null);
              Optional<ButtonType> result = alert.showAndWait();
              if (result.get() == ButtonType.OK) {
                buttonReg.getScene().getWindow().hide();
                loader.setLocation(getClass().getResource("/FXML/authorization.fxml"));

                try {
                  loader.load();
                } catch (IOException e) {
                  e.printStackTrace();
                }

                Parent root = loader.getRoot();
                stage.setScene(new Scene(root));
                stage.show();
              }
            } else if (validationLogin == false) {
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Ошибка");
              alert.setContentText(
                  "Логин, введённый вами уже используются. Пожалуйста, введите другие данные.");
              alert.setHeaderText(null);
              alert.showAndWait();
              passReg.clear();
              loginReg.clear();
              secondNameReg.clear();
              nameReg.clear();
              mailReg.clear();
            }
          }
        });
  }
}
