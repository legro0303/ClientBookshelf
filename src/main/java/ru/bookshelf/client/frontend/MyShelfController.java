package ru.bookshelf.client.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.bookshelf.client.domain.entity.UploadedBook;
import ru.bookshelf.client.service.FileUploadServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MyShelfController {

  @FXML private ResourceBundle resources;

  @FXML private URL location;

  @FXML private Button myShelfBackButton;

  @FXML private TableView<UploadedBook> libTable;

  @FXML private TableColumn<UploadedBook, String> author;

  @FXML private TableColumn<UploadedBook, String> title;

  @FXML private TableColumn<UploadedBook, String> publish_date;

  @FXML private TableColumn<UploadedBook, byte[]> file_data;

  @FXML private ScrollBar dataScroll;

  private String path = new String();

  FXMLLoader loader = new FXMLLoader();
  Stage stage = new Stage();

    public MyShelfController() {
    }

    @FXML
  void initialize() {
    String nodeOfBooks = new String();

    try {
      nodeOfBooks =
          Unirest.get("http://localhost:8080/bookshelf/books")
              .header("accept", "application/json")
              .asString()
              .getBody();

    } catch (UnirestException e) {
      e.printStackTrace();
    }
    ObjectMapper mapper = new ObjectMapper();

    List<UploadedBook> booksFromJson = new ArrayList<>();

    try {
      booksFromJson = Arrays.asList(mapper.readValue(nodeOfBooks, UploadedBook[].class));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    author.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("author"));
    title.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("title"));
    publish_date.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("publish_date"));
    file_data.setCellValueFactory(new PropertyValueFactory<UploadedBook, byte[]>("file_data"));
    libTable.getItems().addAll(booksFromJson);


    FileUploadServiceImpl fileUploadServiceImpl = new FileUploadServiceImpl();

    libTable.setRowFactory(
        tv -> {
          TableRow<UploadedBook> row = new TableRow<>();
          row.setOnMouseClicked(
              event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                  UploadedBook rowData = row.getItem();
                    byte[] fileFromDB = rowData.getFile_data();
                    path = fileUploadServiceImpl.convertToFile(fileFromDB);

                }
              });
          return row;
        });

    myShelfBackButton.setOnAction(
        actionEvent -> {
          // books = (Long) countOfBooksResult.getObject().get("count");
          myShelfBackButton.getScene().getWindow().hide();

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
  }
}
