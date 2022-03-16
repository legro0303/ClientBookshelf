package ru.bookshelf.client.frontend;

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
import ru.bookshelf.client.service.dto.UserRegDTO;

import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/registration.fxml")
public class RegController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private WebClient webClient;

    private final AlertService alertService;

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

    public RegController(AlertService alertService) {
        this.alertService = alertService;
    }

    @FXML
    void initialize() {
        backToAuthButtonReg.setOnAction(
                actionEvent -> {
                        setScene(backToAuthButtonReg, "Авторизация", AuthController.class, fxWeaver);
                });

        buttonReg.setOnAction(
                actionEvent -> {
                    if (firstNameReg.getText().trim().isEmpty()
                            ||secondNameReg.getText().trim().isEmpty()
                            || loginReg.getText().trim().isEmpty()
                            || passReg.getText().trim().isEmpty()
                            || mailReg.getText().trim().isEmpty()) {
                        alertService.showAlert(Alert.AlertType.INFORMATION, "Ошибка", "Вы заполнили не все поля", false);
                    } else {
                        UserAuthDTO userAuthDTO = UserAuthDTO
                                .builder()
                                .login(loginReg.getText())
                                .build();

                        Boolean notRegisteredYet = webClient.post()
                                .uri("http://localhost:10120/message/validation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userAuthDTO)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .block();

                        if (notRegisteredYet == true) {
                            UserRegDTO userRegDTO = UserRegDTO
                                    .builder()
                                    .firstName(firstNameReg.getText())
                                    .secondName(secondNameReg.getText())
                                    .login(loginReg.getText())
                                    .password(passReg.getText())
                                    .mail(mailReg.getText())
                                    .build();

                            webClient.post()
                                    .uri("http://localhost:10120/message/registration")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userRegDTO)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .block();

                            Optional<ButtonType> userClickAlert = alertService.showAlert(Alert.AlertType.INFORMATION, "Вы зарегистрированы", "Успех! Вы зарегистрированы", true);
                            if (userClickAlert.get() == ButtonType.OK) {setScene(backToAuthButtonReg, "Авторизация", AuthController.class, fxWeaver);}
                        } else if (notRegisteredYet == false) {
                            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "Логин, введённый вами уже используются. Пожалуйста, введите другие данные.", false);
                            clearFields(firstNameReg, secondNameReg, loginReg, passReg, mailReg);
                        }
                    }
                });
    }
}
