package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController {

  @FXML private ResourceBundle resources;

  @FXML private URL location;

  @FXML private Hyperlink loadLinkMenu;

  @FXML private Hyperlink myShelfLinkMenu;

  @FXML private Hyperlink backLinkMenu;

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();

  @FXML
  void initialize() {

    loadLinkMenu.setOnAction(
        actionEvent -> {
          loadLinkMenu.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/loadBook.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });

    myShelfLinkMenu.setOnAction(
        actionEvent -> {
          myShelfLinkMenu.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/myShelf.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });

    backLinkMenu.setOnAction(
        actionEvent -> {
          backLinkMenu.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/authorization.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });
  }
}
