package ru.bookshelf.client.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.bookshelf.client.service.FileUploadService;
import ru.bookshelf.client.service.dto.BookDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/myShelf.fxml")
public class MyShelfController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @FXML private Button myShelfBackButton;
    @FXML private TableView<BookDTO> libTable;
    @FXML private TableColumn<BookDTO, String> author;
    @FXML private TableColumn<BookDTO, String> title;
    @FXML private TableColumn<BookDTO, String> publishDate;
    @FXML private TableColumn<BookDTO, byte[]> fileData;
    @FXML private ScrollBar dataScroll;

    private String path = new String();

    private final String bookGet;

    public MyShelfController( @Value("${bookshelf.book.get}") String bookGet) {
        this.bookGet = bookGet;
    }

    @FXML
    void initialize() {
        String nodeOfBooks = new String();

        try {//TODO переделать запрос и добавить обработку ошибки
            nodeOfBooks =
                    Unirest.get(bookGet)
                            .header("accept", "application/json")
                            .asString()
                            .getBody();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();

        List<BookDTO> booksFromJson = new ArrayList<>();

        try {
            booksFromJson = Arrays.asList(mapper.readValue(nodeOfBooks, BookDTO[].class));
        } catch (JsonProcessingException e) {
            log.error("Невозможно считать книгу - [{}] ", e);
        }

        author.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("title"));
        publishDate.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("publishDate"));
        fileData.setCellValueFactory(new PropertyValueFactory<BookDTO, byte[]>("fileData"));
        libTable.getItems().addAll(booksFromJson);


        FileUploadService fileUploadService = new FileUploadService();

        libTable.setRowFactory(
                tv -> {
                    TableRow<BookDTO> row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                    BookDTO rowData = row.getItem();
                                    byte[] fileFromDB = rowData.getFileData();
                                    path = fileUploadService.convertToFile(fileFromDB);

                                }
                            });
                    return row;
                });

        myShelfBackButton.setOnAction(
                actionEvent -> {
                        setScene(myShelfBackButton,"Главное меню", MainMenuController.class, fxWeaver);
                    // books = (Long) countOfBooksResult.getObject().get("count");
                });
    }
}
