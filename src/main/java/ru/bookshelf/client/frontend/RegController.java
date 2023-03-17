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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.bookshelf.client.config.AppConfiguration;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.MailService;
import ru.bookshelf.client.service.dto.UserAuthDTO;
import ru.bookshelf.client.service.dto.UserRegDTO;

import javax.annotation.PostConstruct;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/registration.fxml")
public class RegController extends BaseController
{
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    private WebClient webClient;

    @FXML
    private Button backButton;
    @FXML
    private Button registerUserButton;
    @FXML
    private TextField mailTf;
    @FXML
    private TextField loginTf;
    @FXML
    private TextField firstNameTf;
    @FXML
    private TextField secondNameTf;
    @FXML
    private PasswordField passwordPf;

    private final String userReg;
    private final String userValid;
    private final MailService mailService;
    private final AlertService alertService;

    private final AppConfiguration appConfiguration;

    private AppConfiguration.EmailConfig emailConfig;

    public RegController(@Value("${libraryserv.user.registration}") String userReg,
                         @Value("${libraryserv.user.validation}") String userValid,
                         MailService mailService, AlertService alertService,
                         AppConfiguration appConfiguration)
    {
        this.userReg = userReg;
        this.userValid = userValid;
        this.mailService = mailService;
        this.alertService = alertService;
        this.appConfiguration = appConfiguration;
    }

    @PostConstruct
    public void init() {
        emailConfig = appConfiguration.getEmail();
    }

    @FXML
    void initialize() {
        backButton.setOnAction(
                actionEvent ->
                {
                    setScene(backButton, "Authorization", AuthController.class, fxWeaver);
                });

        registerUserButton.setOnAction(
                actionEvent ->
                {
//                    if (firstNameTf.getText().trim().isEmpty()
//                            || secondNameTf.getText().trim().isEmpty()
//                            || loginTf.getText().trim().isEmpty()
//                            || passwordPf.getText().trim().isEmpty()
//                            || mailTf.getText().trim().isEmpty()) {
//                        alertService.showAlert(Alert.AlertType.INFORMATION, "Пустые поля при регистрации", "Вы заполнили не все поля", false);
//                    } else {
                    UserAuthDTO userAuthDTO = UserAuthDTO
                            .builder()
                            .login(loginTf.getText())
                            .password(passwordPf.getText())
                            .build();
                    try
                    {
                        ResponseEntity<String> notRegisteredYet = webClient.post()
                                .uri(userValid)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(userAuthDTO)
                                .retrieve()
                                .toEntity(String.class)
                                .onErrorResume(WebClientRequestException.class, exception ->
                                {
                                    log.error("Connection to server was lost when user tried to register with login [{}]", userAuthDTO.getLogin());
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
                                                    setScene(registerUserButton, "Start menu", StartController.class, fxWeaver);
                                                }
                                            }
                                            catch (NoSuchElementException e)
                                            {
                                                setScene(registerUserButton, "Start menu", StartController.class, fxWeaver);
                                            }
                                        }
                                    });
                                    return Mono.empty();
                                })
                                .block();
//TODO Возникает NullPointerException когда отсутствует подключение к серверу
                        if (notRegisteredYet.getStatusCode() == HttpStatus.OK)
                        {
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
                                    .doOnSuccess(response ->
                                    {
                                        Platform.runLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Optional<ButtonType> userClickAlert = alertService.showAlert(Alert.AlertType.INFORMATION,
                                                        "Successful registration",
                                                        "Success! Are you registered now",
                                                        true);
                                                if (userClickAlert.get() == ButtonType.OK)
                                                {
                                                    setScene(backButton, "Authorization", AuthController.class, fxWeaver);
                                                }
                                            }
                                        });
                                    })
                                    .onErrorResume(WebClientRequestException.class, exception ->
                                    {
                                        log.error("Connection to server was lost when user tried to register with login [{}]", userAuthDTO.getLogin());
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
                                                        setScene("Start menu", StartController.class, fxWeaver);
                                                    }
                                                }
                                                catch (NoSuchElementException e)
                                                {
                                                    setScene("Start menu", StartController.class, fxWeaver);
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
                                                    alertService.showAlert(Alert.AlertType.ERROR,
                                                            "Uncorrected input data",
                                                            exception.getResponseBodyAsString(),
                                                            false);
                                                }
                                            });
                                            return Mono.empty();
                                        }
                                        return Mono.error(exception);
                                    })
                                    .block();
                        }
                    }
                    catch (WebClientResponseException webClientResponseException)
                    {
                        if ((webClientResponseException.getStatusCode() == HttpStatus.BAD_REQUEST) ||
                                (webClientResponseException.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                        )
                            log.error("Uncorrected input data: [{}], reason [{}]",
                                    webClientResponseException.getResponseBodyAsString().replaceAll("\n", " "),
                                    webClientResponseException.getMessage());

                        alertService.showAlert(Alert.AlertType.ERROR,
                                "Uncorrected input data",
                                webClientResponseException.getResponseBodyAsString(),
                                false);

                        clearFields(firstNameTf, secondNameTf, loginTf, passwordPf, mailTf);
                    }
                });
    }
}
