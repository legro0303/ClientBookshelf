package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.bookshelf.client.domain.entity.Person;
import ru.bookshelf.client.service.AlertService;

import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/registration.fxml")
public class RegController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML private Button buttonReg;
    @FXML private Button backToAuthButtonReg;
    @FXML private TextField firstNameReg;
    @FXML private TextField secondNameReg;
    @FXML private TextField loginReg;
    @FXML private TextField mailReg;
    @FXML private PasswordField passReg;

    @FXML
    void initialize() {
        backToAuthButtonReg.setOnAction(
                actionEvent -> {
                    try {
                        setScene(backToAuthButtonReg,"Авторизация", AuthController.class, fxWeaver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                                try {
                                    setScene(backToAuthButtonReg, "Авторизация", AuthController.class, fxWeaver);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (validationLogin == false) {
                            AlertService alertService = new AlertService();
                            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "Логин, введённый вами уже используются. Пожалуйста, введите другие данные.", false);
                            clearFields(firstNameReg, secondNameReg, loginReg, passReg, mailReg);
                        }
                    }
                });
    }
}
