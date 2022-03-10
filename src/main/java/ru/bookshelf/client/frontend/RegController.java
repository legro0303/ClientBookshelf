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

import java.util.Optional;

public class RegController {


    @FXML
    private Button buttonReg;
    @FXML
    private Button backToAuthButtonReg;
    @FXML
    private TextField firstNameReg;
    @FXML
    private TextField secondNameReg;
    @FXML
    private TextField loginReg;
    @FXML
    private TextField mailReg;
    @FXML
    private PasswordField passReg;


    FXMLLoader loader = new FXMLLoader();
    Stage stage = new Stage();

    @FXML
    void initialize() {
        backToAuthButtonReg.setOnAction(
                actionEvent -> {
                    LoadSceneService loadSceneService = new LoadSceneService();
                    loadSceneService.setScene(loader, backToAuthButtonReg, "/FXML/authorization.fxml",
                            stage, "Авторизация");
                });

        buttonReg.setOnAction(
                actionEvent -> {
                    if (passReg.getText().trim().isEmpty()
                            || loginReg.getText().trim().isEmpty()
                            || secondNameReg.getText().trim().isEmpty()
                            || firstNameReg.getText().trim().isEmpty()
                            || mailReg.getText().trim().isEmpty()) {
                        AlertService alertService = new AlertService();
                        alertService.showAlert(Alert.AlertType.INFORMATION, "Ошибка", "Вы заполнили не все поля", false);
                    } else {
                        Person person = new Person();
                        person.setFirstName(firstNameReg.getText());
                        person.setSecondName(secondNameReg.getText());
                        person.setLogin(loginReg.getText());
                        person.setMail(mailReg.getText());
                        person.setPassword(passReg.getText());
                        JsonNode validationResult = new JsonNode(null);
                        Boolean validationLogin;
                        try {
                            validationResult =
                                    Unirest.post("http://localhost:8080/message/validation")
                                            .header("accept", "application/json")
                                            .field("firstName", person.getFirstName())
                                            .field("secondName", person.getSecondName())
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
                                        .field("firstName", person.getFirstName())
                                        .field("secondName", person.getSecondName())
                                        .field("login", person.getLogin())
                                        .field("mail", person.getMail())
                                        .field("password", person.getPassword())
                                        .asJson();
                            } catch (UnirestException e) {
                                e.printStackTrace();
                            }
                            AlertService alertService = new AlertService();
                            Optional<ButtonType> result = alertService.showAlert(Alert.AlertType.INFORMATION, "Вы зарегистрированы", "Успех! Вы зарегистрированы", true);
                            if (result.get() == ButtonType.OK) {
                                LoadSceneService loadSceneService = new LoadSceneService();
                                loadSceneService.setScene(loader, backToAuthButtonReg, "/FXML/authorization.fxml",
                                        stage, "Авторизация");
                            }
                        } else if (validationLogin == false) {
                            AlertService alertService = new AlertService();
                            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "Логин, введённый вами уже используются. Пожалуйста, введите другие данные.", false);
                            LoadSceneService loadSceneService = new LoadSceneService();
                            loadSceneService.clearFields(firstNameReg, secondNameReg, loginReg, passReg, mailReg);
                        }
                    }
                });
    }
}
