package ru.bookshelf.client.frontend;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/library.fxml")
public class LibraryController extends BaseController {
    @Autowired private FxWeaver fxWeaver;
    @Autowired private WebClient webClient;
    @Autowired private PDFDisplayer displayer;

    @FXML private ScrollBar dataScroll;
    @FXML private Button backButton;
    @FXML private Button deletingButton;
    @FXML private TableView<BookDTO> libTable;
    @FXML private TableColumn<BookDTO, Long> id;
    @FXML private TableColumn<BookDTO, String> title;
    @FXML private TableColumn<BookDTO, String> author;
    @FXML private TableColumn<BookDTO, String> publishDate;

    private final AlertService alertService;
    private final FileUploadService fileUploadService;
    private final UserAuthRepository userAuthRepository;
    private final String bookGet;
    private final String bookGetBytes;
    private final String bookDelete;

    public LibraryController(AlertService alertService,
                             FileUploadService fileUploadService, UserAuthRepository userAuthRepository,
                             @Value("${libraryserv.book.get}") String bookGet,
                             @Value("${libraryserv.book.get-bytes}") String bookGetBytes,
                             @Value("${libraryserv.book.delete}") String bookDelete) {
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
                tableView -> {
                    TableRow<BookDTO> row = new TableRow<>();
                    row.setOnMouseClicked(
                            event -> {
                                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                    byte[] bytesOfBook = webClient.get()
                                            .uri(bookGetBytes + row.getItem().getId())
                                            .retrieve()
                                            .bodyToMono(byte[].class)
                                            .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для получения всех книг - [{}]", exception.getMessage()))
                                            .block();
                                    try {
                                        //TODO доделать
                                        Stage stage = new Stage();
                                        displayer.loadPDF(fileUploadService.convertToFile(bytesOfBook));
                                        Parent root = displayer.toNode();
                                        Scene scene = new Scene(root);
                                        stage.setTitle("23423");
                                        stage.setResizable(false);
                                        stage.setScene(scene);
                                        stage.show();
                                        //setScene("Библиотека123", displayer, fxWeaver);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    return row;
                });

        backButton.setOnAction(
                actionEvent -> {
                    setScene(backButton, "Главное меню", MainMenuController.class, fxWeaver);
                });
        deletingButton.setOnAction(
                actionEvent -> {
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
                });
    }
}
