package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.bookshelf.client.service.repository.UserAuthRepository;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/mainMenu.fxml")
public class MainMenuController extends BaseController {
    @Autowired private FxWeaver fxWeaver;

    @FXML private Hyperlink backLink;
    @FXML private Hyperlink libraryLink;
    @FXML private Hyperlink loadingBookLink;

    private final UserAuthRepository userAuthRepository;

    public MainMenuController(UserAuthRepository userAuthRepository) {
        this.userAuthRepository = userAuthRepository;
    }

    @FXML
    void initialize() {
        loadingBookLink.setOnAction(
                actionEvent ->
                {
                    setScene(loadingBookLink, "Upload books", LoadBookController.class, fxWeaver);
                });

        libraryLink.setOnAction(
                actionEvent ->
                {
                    setScene(libraryLink, "Library", LibraryController.class, fxWeaver);
                });

        backLink.setOnAction(
                actionEvent ->
                {
                    userAuthRepository.deleteUser();
                    setScene(backLink, "Authorization", AuthController.class, fxWeaver);
                });
    }
}
