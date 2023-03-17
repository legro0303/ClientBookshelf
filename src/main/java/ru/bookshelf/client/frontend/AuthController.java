package ru.bookshelf.client.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.bookshelf.client.config.AppConfiguration;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.MailService;
import ru.bookshelf.client.service.dto.UserAuthDTO;
import ru.bookshelf.client.service.repository.UserAuthRepository;

import javax.annotation.PostConstruct;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/authorization.fxml")
public class AuthController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    private WebClient webClient;

    @FXML
    private Button authButton;
    @FXML
    private TextField loginTf;
    @FXML
    private PasswordField passwordPf;
    @FXML
    private Hyperlink registrationLink;

    private final String USER_AUTH;
    private final MailService mailService;
    private final AlertService alertService;
    private final UserAuthRepository userAuthRepository;

    private final AppConfiguration appConfiguration;

    private AppConfiguration.EmailConfig emailConfig;

    public AuthController(@Value("${libraryserv.user.authorization}") String userAuth,
                          MailService mailService,
                          AlertService alertService,
                          UserAuthRepository userAuthRepository,
                          AppConfiguration appConfiguration) {
        this.USER_AUTH = userAuth;
        this.mailService = mailService;
        this.alertService = alertService;
        this.userAuthRepository = userAuthRepository;
        this.appConfiguration = appConfiguration;
    }

    @PostConstruct
    public void init() {
        emailConfig = appConfiguration.getEmail();
    }

    @FXML
    void initialize() {
        registrationLink.setOnAction(
                actionEvent ->
                {
                    setScene(registrationLink, "Registration", RegController.class, fxWeaver);
                });

        authButton.setOnAction(
                actionEvent ->
                {
                    if (loginTf.getText().trim().isEmpty() || passwordPf.getText().trim().isEmpty())
                    {
                        alertService.showAlert(Alert.AlertType.ERROR,
                                "Empty fields error",
                                "Login or password which was entered are empty",
                                false);

                        clearFields(loginTf, passwordPf);
                    }
                    else
                    {
                        UserAuthDTO userAuthDTO = UserAuthDTO
                                .builder()
                                .login(loginTf.getText())
                                .password(passwordPf.getText())
                                .build();

                        userAuthRepository.addUser(userAuthDTO);

                        webClient.post()
                                .uri(USER_AUTH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userAuthDTO)
                                .retrieve()
                                .bodyToMono(String.class)
                                .doOnSuccess(response ->
                                {
                                    log.info("User [{}] was authorized successfully!", userAuthDTO.getLogin());
                                    Platform.runLater(new Runnable()
                                    {
                                        public void run()
                                        {
                                            setScene(authButton, "Main menu", MainMenuController.class, fxWeaver);
                                        }
                                    });
                                })
                                .onErrorResume(WebClientRequestException.class, exception ->
                                {
                                    log.error("Connection to server was lost when user [{}] tried to authorize", userAuthDTO.getLogin());
                                    Platform.runLater(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Optional<ButtonType> userClickAlert = alertService.showAlert(
                                                    Alert.AlertType.ERROR,
                                                    "Error connection",
                                                    "Connection to server was lost, press OK button for return to start menu",
                                                    true);
                                            try
                                            {
                                                if ((userClickAlert.get() == ButtonType.OK))
                                                {
                                                    setScene(authButton, "Start menu", StartController.class, fxWeaver);
                                                }
                                            }
                                            catch (NoSuchElementException e)
                                            {
                                                setScene(authButton, "Start menu", StartController.class, fxWeaver);
                                            }
                                        }
                                    });
                                    return Mono.empty();
                                })
                                .onErrorResume(WebClientResponseException.class, exception ->
                                {
                                    if (exception.getStatusCode() == HttpStatus.BAD_REQUEST)
                                    {
                                        log.error("Uncorrected input data: [{}], reason [{}]",
                                                exception.getResponseBodyAsString().replaceAll("\n", " "),
                                                exception.getMessage());
                                        Platform.runLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                alertService.showAlert(Alert.AlertType.ERROR, "Uncorrected input data",
                                                        exception.getResponseBodyAsString(), false);
                                            }
                                        });
                                        return Mono.empty();
                                    }
                                    else if (exception.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                                    {
                                        log.error("The user with the specified combination of password and login was not found [{}]", exception.getMessage());
                                        Platform.runLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                alertService.showAlert(Alert.AlertType.ERROR,"Authorization error",
                                                        exception.getResponseBodyAsString(),false);

                                                clearFields(loginTf, passwordPf);
                                            }
                                        });
                                        return Mono.empty();
                                    }
                                    return Mono.error(exception);
                                })
                                .block();
                    }
                });
    }
}
