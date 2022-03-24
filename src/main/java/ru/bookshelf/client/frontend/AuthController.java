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
import ru.bookshelf.client.service.repository.UserAuthRepository;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/authorization.fxml")
public class AuthController extends BaseController {
    @Autowired private FxWeaver fxWeaver;
    @Autowired private WebClient webClient;

    @FXML private Button authButton;
    @FXML private TextField loginTf;
    @FXML private PasswordField passwordPf;
    @FXML private Hyperlink registrationLink;

    private final String authUser;
    private final AlertService alertService;
    private final UserAuthRepository userAuthRepository;

    public AuthController(@Value("${bookshelf.user.authorization}") String authUser, AlertService alertService, UserAuthRepository userAuthRepository) {
        this.authUser = authUser;
        this.alertService = alertService;
        this.userAuthRepository = userAuthRepository;
    }

    @FXML
    void initialize() {
        registrationLink.setOnAction(
                actionEvent -> {
                    setScene(registrationLink, "Регистрация", RegController.class, fxWeaver);
                });

        authButton.setOnAction(
                actionEvent -> {
                    UserAuthDTO userAuthDTO = UserAuthDTO
                            .builder()
                            .login(loginTf.getText())
                            .password(passwordPf.getText())
                            .build();
                    userAuthRepository.addUser(userAuthDTO);

                    Boolean result = webClient.post()
                            .uri(authUser)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userAuthDTO)
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для авторизации пользователя - [{}]", exception.getMessage()))
                            .block();
                    if (result == true) {
                        setScene(authButton, "Главное меню", MainMenuController.class, fxWeaver);
                    } else {
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка авторизации",
                                "Вы ввели неверный логин или пароль, пожалуйста, попробуйте ввести данные ещё раз.", false);
                        clearFields(loginTf, passwordPf);
                    }
                });
    }
}
