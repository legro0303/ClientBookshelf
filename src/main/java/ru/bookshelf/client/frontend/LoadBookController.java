package ru.bookshelf.client.frontend;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.body.MultipartBody;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.bookshelf.client.domain.entity.UploadedBook;
import ru.bookshelf.client.service.FileUploadServiceImpl;


import java.io.*;

public class LoadBookController {

  @FXML private Button loadBookBackButton;

  @FXML private Button loadBookButton;

  @FXML private TextField tbAuthor;

  @FXML private Label labelAuthor;

  @FXML private TextField tbTitle;

  @FXML private Label labelTitle;

  @FXML private TextField tbPublishDate;

  @FXML private Label labelPublishDate;

  @FXML private Button saveBookButton;

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();
  FileChooser fileChooser = new FileChooser();
  File choosedFile;
  InputStream file;

  @FXML
  void initialize() {
    UploadedBook uploadedBook = new UploadedBook();

    FileUploadServiceImpl fileUploadServiceImpl = new FileUploadServiceImpl();

    loadBookBackButton.setOnAction(
        actionEvent -> {
          loadBookBackButton.getScene().getWindow().hide();
          loader.setLocation(getClass().getResource("/FXML/mainMenu.fxml"));

          try {
            loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }

          Parent root = loader.getRoot();
          stage.setScene(new Scene(root));
          stage.show();
        });

    loadBookButton.setOnAction(
        actionEvent -> {
          FileChooser.ExtensionFilter extFilter =
              new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
          fileChooser.getExtensionFilters().add(extFilter);

          fileChooser.setTitle("Download book");
          choosedFile = fileChooser.showOpenDialog(stage);
        });
    saveBookButton.setOnAction(
        actionEvent -> {
          if (tbAuthor.getText().trim().isEmpty()
              || tbTitle.getText().trim().isEmpty()
              || tbPublishDate.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setContentText("Вы заполнили не все поля");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane
                .getStylesheets()
                .add(getClass().getResource("AlertStyles.css").toExternalForm());
            dialogPane.getStyleClass().add("AlertStyles");
            //                DialogPane dialogPane = alert.getDialogPane();
            //                dialogPane.setStyle("-fx-background-color: rgb(33,33,33);");
            alert.showAndWait();
          } else {


              uploadedBook.setAuthor(tbAuthor.getText());
              uploadedBook.setTitle(tbTitle.getText());
              uploadedBook.setPublish_date(tbPublishDate.getText());
              //uploadedBook.setFile_data(fileUploadServiceImpl.uploadToServer(choosedFile));

//              MultipartBody bookLoadResult;
//              bookLoadResult =
//                  Unirest.post("http://localhost:8080/bookshelf/add")
//                      .header("accept", "application/json")
//                      .field("author", uploadedBook.getAuthor())
//                      .field("title", uploadedBook.getTitle())
//                      .field("publish_date", uploadedBook.getPublish_date())
//                      .field("file_data",new File(choosedFile.getName()));

              try {
                   file = new FileInputStream(new File(choosedFile.getAbsolutePath()));
              } catch (FileNotFoundException e) {
                  e.printStackTrace();
              }
//
//              try {
//
//                  MultipartBody body = Unirest.post("http://localhost:8080/bookshelf/add")
//                          .header("Content-Type", "application/json,multipart/form-data")
//                          .field("author", uploadedBook.getAuthor())
//                          .field("title", uploadedBook.getTitle())
//                          .field("publish_date", uploadedBook.getPublish_date())
//                          .field("file_data", new File (choosedFile.getAbsolutePath()));
//
//                    int i = 0;
//              } catch (Exception e) {
//                  e.printStackTrace();
//              }
//
              try {
                  MultipartBody body = Unirest.post("http://localhost:8080/bookshelf/add")
                          .field("author", uploadedBook.getAuthor())
                          .field("title", uploadedBook.getTitle())
                          .field("publish_date", uploadedBook.getPublish_date())
                          .field("file_data", new File (choosedFile.getAbsolutePath()));
                  HttpResponse<String> file = body.asString();
                  System.out.println(file.getStatus());

              } catch (Exception e) {
                  e.printStackTrace();
              }

          }
        });
  }
}
