package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.dto.UserAuthDTO;
import ru.bookshelf.client.service.dto.UserRegDTO;

import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/registration.fxml")
public class RegController extends BaseController {
    @Autowired private FxWeaver fxWeaver;
    @Autowired private WebClient webClient;

    @FXML private Button backButton;
    @FXML private Button registerUserButton;
    @FXML private TextField mailTf;
    @FXML private TextField loginTf;
    @FXML private TextField firstNameTf;
    @FXML private TextField secondNameTf;
    @FXML private PasswordField passwordPf;

    private final String userReg;
    private final String userValid;
    private final AlertService alertService;

    public RegController(@Value("${libraryserv.user.registration}") String userReg,
                         @Value("${libraryserv.user.validation}") String userValid,
                         AlertService alertService) {
        this.userReg = userReg;
        this.userValid = userValid;
        this.alertService = alertService;
    }

    @FXML
    void initialize() {
        backButton.setOnAction(
                actionEvent -> {
                    setScene(backButton, "Авторизация", AuthController.class, fxWeaver);
                });

        registerUserButton.setOnAction(
                actionEvent -> {
                    if (firstNameTf.getText().trim().isEmpty()
                            || secondNameTf.getText().trim().isEmpty()
                            || loginTf.getText().trim().isEmpty()
                            || passwordPf.getText().trim().isEmpty()
                            || mailTf.getText().trim().isEmpty()) {
                        alertService.showAlert(Alert.AlertType.INFORMATION, "Пустые поля при регистрации", "Вы заполнили не все поля", false);
                    } else {
                        UserAuthDTO userAuthDTO = UserAuthDTO
                                .builder()
                                .login(loginTf.getText())
                                .build();

                        Boolean notRegisteredYet = webClient.post()
                                .uri(userValid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userAuthDTO)
                                .retrieve()
                                .bodyToMono(Boolean.class)
                                .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для проверки создан ли пользователь - [{}]", exception.getMessage()))
                                .block();

                        if (notRegisteredYet == true) {
                            UserRegDTO userRegDTO = UserRegDTO
                                    .builder()
                                    .firstName(firstNameTf.getText())
                                    .secondName(secondNameTf.getText())
                                    .login(loginTf.getText())
                                    .password(passwordPf.getText())
                                    .mail(mailTf.getText())
                                    .build();

                            webClient.post()
                                    .uri(userReg)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(userRegDTO)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для регистрации пользователя - [{}]", exception.getMessage()))
                                    .block();

                            Optional<ButtonType> userClickAlert = alertService.showAlert(Alert.AlertType.INFORMATION, "Вы зарегистрированы", "Успех! Вы зарегистрированы", true);
                            if (userClickAlert.get() == ButtonType.OK) {
                                setScene(backButton, "Авторизация", AuthController.class, fxWeaver);
                            }
                        } else {
                            alertService.showAlert(Alert.AlertType.ERROR, "Пользователь уже зарегистрирован", "Логин, введённый вами уже используются. Пожалуйста, введите другие данные.", false);
                            clearFields(firstNameTf, secondNameTf, loginTf, passwordPf, mailTf);
                        }
                    }
                });
    }
}
