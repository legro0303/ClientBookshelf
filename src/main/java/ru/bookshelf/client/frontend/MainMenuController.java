package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/mainMenu.fxml")
public class MainMenuController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML private Hyperlink loadLinkMenu;
    @FXML private Hyperlink myShelfLinkMenu;
    @FXML private Hyperlink backLinkMenu;

    @FXML
    void initialize() {

        loadLinkMenu.setOnAction(
                actionEvent -> {
                    setScene(loadLinkMenu, "Загрузка книг", LoadBookController.class, fxWeaver);
                });

        myShelfLinkMenu.setOnAction(
                actionEvent -> {
                    setScene(myShelfLinkMenu, "Библиотека книг", MyShelfController.class, fxWeaver);
                });

        backLinkMenu.setOnAction(
                actionEvent -> {
                    setScene(myShelfLinkMenu, "Авторизация", AuthController.class, fxWeaver);
                });
    }
}
