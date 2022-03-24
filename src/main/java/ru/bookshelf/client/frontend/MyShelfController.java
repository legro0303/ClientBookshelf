package ru.bookshelf.client.frontend;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.event.ActionEvent;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.FileUploadService;
import ru.bookshelf.client.service.dto.BookDTO;
import ru.bookshelf.client.service.repository.UserAuthRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/myShelf.fxml")
public class MyShelfController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private WebClient webClient;

    private final AlertService alertService;
    private final FileUploadService fileUploadService;
    private final UserAuthRepository userAuthRepository;

    @FXML
    private Button myShelfBackButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<BookDTO> libTable;
    @FXML
    private TableColumn<BookDTO, Long> id;
    @FXML
    private TableColumn<BookDTO, String> author;
    @FXML
    private TableColumn<BookDTO, String> title;
    @FXML
    private TableColumn<BookDTO, String> publishDate;
    @FXML
    private ScrollBar dataScroll;

    private String path = new String();

    private final String bookGet;
    private final String bookGetBytes;
    private final String bookDelete;


    public MyShelfController(AlertService alertService,
                             FileUploadService fileUploadService, UserAuthRepository userAuthRepository,
                             @Value("${bookshelf.book.get}") String bookGet,
                             @Value("${bookshelf.book.get-book-bytes}") String bookGetBytes,
                             @Value("${bookshelf.book.delete}") String bookDelete) {
        this.alertService = alertService;
        this.fileUploadService = fileUploadService;
        this.userAuthRepository = userAuthRepository;
        this.bookGet = bookGet;
        this.bookGetBytes = bookGetBytes;
        this.bookDelete = bookDelete;
    }

    @FXML
    void initialize() {
        List<BookDTO> booksList = webClient.get()
                .uri(bookGet)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BookDTO>>() {
                })
                .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для получения всех книг - [{}]", exception.getMessage()))
                .block();

        id.setCellValueFactory(new PropertyValueFactory<BookDTO, Long>("id"));
        author.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("title"));
        publishDate.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("publishDate"));

        libTable.getItems().addAll(booksList);


        log.info("libtable имеет [{}]", libTable.getItems());

        libTable.setRowFactory(
                tv -> {
                    TableRow<BookDTO> row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                    byte[] qwe = webClient.get()
                                            .uri(bookGetBytes + row.getItem().getId())
                                            .retrieve()
                                            .bodyToMono(byte[].class)
                                            .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для получения всех книг - [{}]", exception.getMessage()))
                                            .block();
                                    path = fileUploadService.convertToFile(qwe);
                                    PDFDisplayer displayer = new PDFDisplayer();
                                    File file = new File(path);
                                    try {
                                        displayer.loadPDF(file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    setScene("Библиотека123", displayer, fxWeaver);
                                    file.delete();
                                }
                            });
                    return row;
                });

        myShelfBackButton.setOnAction(
                actionEvent -> {
                    setScene(myShelfBackButton, "Главное меню", MainMenuController.class, fxWeaver);
                    // books = (Long) countOfBooksResult.getObject().get("count");
                });
    }

    @FXML
    void deleteRowAction(ActionEvent event) {
        BookDTO bookDTO = libTable.getSelectionModel().getSelectedItem();
        bookDTO.setOwner(userAuthRepository.getUser().getLogin());

        Boolean bookWasDeleted = webClient.post()
                .uri(bookDelete)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDTO)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для удаления книги - [{}]", exception.getMessage()))
                .block();

        if (bookWasDeleted == true) {
            libTable.getItems().removeAll(bookDTO);
        } else if (bookWasDeleted == false) {
            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка удаления книги",
                    "Вы не можете удалить данную книгу, так как не являетесь её владельцем", false);
        }
    }
}
