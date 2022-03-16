package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.JsonNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.dto.UserAuthDTO;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/authorization.fxml")
public class AuthController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private WebClient webClient;

    private final AlertService alertService;

    @FXML
    private PasswordField passAuth;
    @FXML
    private TextField loginAuth;
    @FXML
    private Button buttonAuth;
    @FXML
    private Hyperlink linkAuth;

    public AuthController(AlertService alertService) {
        this.alertService = alertService;
    }

    @FXML
    void initialize() {
        linkAuth.setOnAction(
                actionEvent -> {
                    setScene(linkAuth, "Регистрация", RegController.class, fxWeaver);
                });

        buttonAuth.setOnAction(
                actionEvent -> {
                    UserAuthDTO userAuth = UserAuthDTO
                            .builder()
                            .login(loginAuth.getText())
                            .password(passAuth.getText())
                            .build();

                    Boolean result = webClient.post()
                            .uri("http://localhost:10120/message/authorization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userAuth)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();

                    if (result == true) {
                        setScene(buttonAuth, "Главное меню", MainMenuController.class, fxWeaver);
                    } else {
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка",
                                "Вы ввели неверный логин или пароль, пожалуйста, попробуйте ввести данные ещё раз.", false);
                        clearFields(loginAuth, passAuth);
                    }
                });
    }
}
