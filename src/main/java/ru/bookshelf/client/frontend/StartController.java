package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/start.fxml")
public class StartController extends BaseController {
    @Autowired private FxWeaver fxWeaver;
    @Autowired private WebClient webClient;

    @FXML private Button startButton;

    private final AlertService alertService;

    public StartController(AlertService alertService) {
        this.alertService = alertService;
    }

    @FXML
    void initialize() {
        startButton.setOnAction(
                actionEvent -> {
//                    webClient.post()
//                            .uri("http://localhost:10120/book")
//                            .retrieve()
//                            .bodyToMono(Boolean.class)
//                            .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для авторизации пользователя - [{}]", exception.getMessage()))
//                            .block();

                    //alertService.showAlert(Alert.AlertType.ERROR, "Сервер не работает", "В данный момент сервер не функционирует", false);
                    //TODO добавить проверку на доступность сервера
                    setScene(startButton, "Авторизация", AuthController.class, fxWeaver);
                });
    }
}
