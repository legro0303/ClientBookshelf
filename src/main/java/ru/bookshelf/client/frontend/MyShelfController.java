package ru.bookshelf.client.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ru.bookshelf.client.domain.entity.UploadedBook;
import ru.bookshelf.client.service.FileUploadService;
import ru.bookshelf.client.service.LoadSceneService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyShelfController {

    @FXML private Button myShelfBackButton;
    @FXML private TableView<UploadedBook> libTable;
    @FXML private TableColumn<UploadedBook, String> author;
    @FXML private TableColumn<UploadedBook, String> title;
    @FXML private TableColumn<UploadedBook, String> publishDate;
    @FXML private TableColumn<UploadedBook, byte[]> fileData;
    @FXML private ScrollBar dataScroll;

    private String path = new String();

    FXMLLoader loader = new FXMLLoader();
    Stage stage = new Stage();

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
            System.out.println("Невозможно считать книгу, ошибка" + e);
        }

        author.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("title"));
        publishDate.setCellValueFactory(new PropertyValueFactory<UploadedBook, String>("publishDate"));
        fileData.setCellValueFactory(new PropertyValueFactory<UploadedBook, byte[]>("fileData"));
        libTable.getItems().addAll(booksFromJson);


        FileUploadService fileUploadService = new FileUploadService();

        libTable.setRowFactory(
                tv -> {
                    TableRow<UploadedBook> row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                    UploadedBook rowData = row.getItem();
                                    byte[] fileFromDB = rowData.getFileData();
                                    path = fileUploadService.convertToFile(fileFromDB);

                                }
                            });
                    return row;
                });

        myShelfBackButton.setOnAction(
                actionEvent -> {
                    LoadSceneService loadSceneService = new LoadSceneService();
                    loadSceneService.setScene(loader, myShelfBackButton, "/FXML/mainMenu.fxml",
                            stage, "Главное меню");
                    // books = (Long) countOfBooksResult.getObject().get("count");
                });
    }
}
