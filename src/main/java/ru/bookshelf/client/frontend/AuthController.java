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

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/authorization.fxml")
public class AuthController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML private PasswordField passAuth;
    @FXML private TextField loginAuth;
    @FXML private Button buttonAuth;
    @FXML private Hyperlink linkAuth;

    @FXML
    void initialize() {
        linkAuth.setOnAction(
                actionEvent -> {
                    setScene(linkAuth, "Регистрация", RegController.class, fxWeaver);
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
                                Unirest.post("http://localhost:10120/message/authorization")
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
                        try {
                            setScene(buttonAuth,"Главное меню", MainMenuController.class, fxWeaver);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (userAuthorization == false) {
                        AlertService alertService = new AlertService();
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка",
                                "Вы ввели неверный логин или пароль, пожалуйста, попробуйте ввести данные ещё раз.", false);
                    }
                });
    }
}
