package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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
import ru.bookshelf.client.service.AlertService;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/start.fxml")
public class StartController extends BaseController {

    @Autowired private FxWeaver fxWeaver;

    private final AlertService alertService;

    @FXML private Button startButton;

    public StartController(AlertService alertService) {
        this.alertService = alertService;
    }

    @FXML
    void initialize() {
        startButton.setOnAction(
                actionEvent -> {
                    HttpResponse<String> req = null;
                    try {
                        req = Unirest.get("http://localhost:10120/message").asString();//TODO понять как определять работу сервера
                    } catch (UnirestException e) {
                        alertService.showAlert(Alert.AlertType.ERROR, "Сервер не работает", "В данный момент сервер не функционирует", false);
                        e.printStackTrace();
                    }
                        setScene(startButton, "Авторизация", AuthController.class, fxWeaver);
                });
    }
}
