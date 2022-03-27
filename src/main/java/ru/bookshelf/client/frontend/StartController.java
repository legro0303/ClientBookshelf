package ru.bookshelf.client.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import ru.bookshelf.client.config.AppConfiguration;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.MailService;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/start.fxml")
public class StartController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    private WebClient webClient;

    @FXML
    private Button startButton;

    private final MailService mailService;
    private final AlertService alertService;
    private final AppConfiguration appConfiguration;
    private final String healthCheckURL;

    private AppConfiguration.EmailConfig emailConfig;


    public StartController(@Value("${libraryserv.server.health-check}") String healthCheckURL, MailService mailService, AlertService alertService, AppConfiguration appConfiguration) {
        this.mailService = mailService;
        this.alertService = alertService;
        this.appConfiguration = appConfiguration;
        this.healthCheckURL = healthCheckURL;
    }

    @PostConstruct
    public void init() {
        emailConfig = appConfiguration.getEmail();
    }

    @FXML
    void initialize() {
        startButton.setOnAction(
                actionEvent -> {
                    //TODO разобраться с хендлингом исключений внутри веб клиента
                    try {
                        webClient.post()
                                .uri(healthCheckURL)
                                .retrieve()
                                .bodyToMono(Void.class)
                                .doOnSuccess(response -> {
                                    log.info("Сервер доступен!");
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            setScene(startButton, "Авторизация", AuthController.class, fxWeaver);
                                        }
                                    });
                                })
                                .onErrorMap(exception -> {
                                    try {
                                        mailService.sendEmail(emailConfig, exception);
                                    } catch (MessagingException e) {
                                        log.error("Возникла ошибка при попытке отправить уведомление администратору [{}]", e);
                                    }
                                    log.error("Сервер не доступен [{}]", exception.getMessage());
                                    Platform.runLater(new Runnable() {
                                        public void run() {
                                            alertService.showAlert(Alert.AlertType.ERROR, "Сервер не работает", "В данный момент сервер не функционирует", false);
                                        }
                                    });
                                    return exception;
                                })
                                .block();
                    } catch (WebClientRequestException ex) {
                        log.error("Невозможно установить соединение с сервером [{}]", ex.getMessage());
                    }
                });
    }
}
