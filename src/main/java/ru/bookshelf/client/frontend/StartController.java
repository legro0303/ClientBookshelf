package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.bookshelf.client.service.AlertService;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/start.fxml")
public class StartController extends BaseController {

    @Autowired private FxWeaver fxWeaver;

    @FXML private Button startButton;

    @FXML
    void initialize() {
        startButton.setOnAction(
                actionEvent -> {
                    HttpResponse<String> req = null;
                    try {
                        req = Unirest.get("http://localhost:10120/message").asString();
                    } catch (UnirestException e) {
                        AlertService alertService = new AlertService();
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "В данный момент сервер не функционирует", false);
                        e.printStackTrace();
                    }
                    try {
                        setScene(startButton, "Авторизация", AuthController.class, fxWeaver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
