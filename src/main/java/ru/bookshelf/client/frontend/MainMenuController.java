package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import ru.bookshelf.client.service.LoadSceneService;

public class MainMenuController {

  @FXML private Hyperlink loadLinkMenu;
  @FXML private Hyperlink myShelfLinkMenu;
  @FXML private Hyperlink backLinkMenu;

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();

  @FXML
  void initialize() {

    loadLinkMenu.setOnAction(
        actionEvent -> {
            LoadSceneService loadSceneService = new LoadSceneService();
            loadSceneService.setScene(loader, loadLinkMenu, "/FXML/loadBook.fxml",
                    stage, "Загрузка книг");
        });

    myShelfLinkMenu.setOnAction(
        actionEvent -> {
            LoadSceneService loadSceneService = new LoadSceneService();
            loadSceneService.setScene(loader, myShelfLinkMenu, "/FXML/myShelf.fxml",
                    stage, "Библиотека книг");
        });

    backLinkMenu.setOnAction(
        actionEvent -> {
            LoadSceneService loadSceneService = new LoadSceneService();
            loadSceneService.setScene(loader, myShelfLinkMenu, "/FXML/authorization.fxml",
                    stage, "Авторизация");
        });
  }
}
