package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.bookshelf.client.domain.entity.Person;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.LoadSceneService;

public class AuthController {

    @FXML private PasswordField passAuth;
    @FXML private TextField loginAuth;
    @FXML private Button buttonAuth;
    @FXML private Hyperlink linkAuth;

    FXMLLoader loader = new FXMLLoader();
    Stage stage = new Stage();

    @FXML
    void initialize() {

        linkAuth.setOnAction(
                actionEvent -> {
                    LoadSceneService loadSceneService = new LoadSceneService();
                    loadSceneService.setScene(loader, linkAuth, "/FXML/registration.fxml",
                            stage, "Регистрация");
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
                        LoadSceneService loadSceneService = new LoadSceneService();
                        loadSceneService.setScene(loader, buttonAuth, "/FXML/mainMenu.fxml",
                                stage, "Главное меню");
                    } else if (userAuthorization == false) {
                        AlertService alertService = new AlertService();
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка",
                                "Вы ввели неверный логин или пароль, пожалуйста, попробуйте ввести данные ещё раз.");
                    }
                });
    }
}
