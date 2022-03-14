package ru.bookshelf.client.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.bookshelf.client.domain.entity.UploadedBook;
import ru.bookshelf.client.service.FileUploadService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/myShelf.fxml")
public class MyShelfController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML private Button myShelfBackButton;
    @FXML private TableView<UploadedBook> libTable;
    @FXML private TableColumn<UploadedBook, String> author;
    @FXML private TableColumn<UploadedBook, String> title;
    @FXML private TableColumn<UploadedBook, String> publishDate;
    @FXML private TableColumn<UploadedBook, byte[]> fileData;
    @FXML private ScrollBar dataScroll;

    private String path = new String();

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
                    try {
                        setScene(myShelfBackButton,"Главное меню", MainMenuController.class, fxWeaver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // books = (Long) countOfBooksResult.getObject().get("count");
                });
    }
}
