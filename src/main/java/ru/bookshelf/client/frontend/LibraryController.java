package ru.bookshelf.client.frontend;

import javafx.application.Platform;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.bookshelf.client.config.AppConfiguration;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.FileUploadService;
import ru.bookshelf.client.service.MailService;
import ru.bookshelf.client.service.dto.BookDTO;
import ru.bookshelf.client.service.dto.DeleteDTO;
import ru.bookshelf.client.service.repository.UserAuthRepository;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/library.fxml")
public class LibraryController extends BaseController {
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    private WebClient webClient;

    @FXML
    private ScrollBar dataScroll;
    @FXML
    private Button backButton;
    @FXML
    private Button deletingButton;
    @FXML
    private TableView<BookDTO> libTable;
    @FXML
    private TableColumn<BookDTO, Long> id;
    @FXML
    private TableColumn<BookDTO, String> title;
    @FXML
    private TableColumn<BookDTO, String> author;
    @FXML
    private TableColumn<BookDTO, String> publishDate;

    private final MailService mailService;
    private final AlertService alertService;
    private final FileUploadService fileUploadService;
    private final UserAuthRepository userAuthRepository;
    private final String GET_BOOK;
    private final String GET_BOOK_BYTES;
    private final String DELETE_BOOK;
    private final AppConfiguration appConfiguration;
    private AppConfiguration.EmailConfig emailConfig;

    public LibraryController(MailService mailService, AlertService alertService,
                             FileUploadService fileUploadService, UserAuthRepository userAuthRepository,
                             @Value("${libraryserv.book.get}") String getBook,
                             @Value("${libraryserv.book.get-bytes}") String getBookBytes,
                             @Value("${libraryserv.book.delete}") String deleteBook,
                             AppConfiguration appConfiguration) {
        this.mailService = mailService;
        this.alertService = alertService;
        this.fileUploadService = fileUploadService;
        this.userAuthRepository = userAuthRepository;
        this.GET_BOOK = getBook;
        this.GET_BOOK_BYTES = getBookBytes;
        this.DELETE_BOOK = deleteBook;
        this.appConfiguration = appConfiguration;
    }
    @PostConstruct
    public void init() {
        emailConfig = appConfiguration.getEmail();
    }

    @FXML
    void initialize() {
        List<BookDTO> booksList = webClient.get()
                .uri(GET_BOOK)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BookDTO>>() {})
                .doOnError(exception -> log.error("Error when trying to send a request to the server to get all books [{}]", exception.getMessage()))
                .onErrorResume(WebClientRequestException.class, exception ->
                {
                    Platform.runLater(new Runnable()
                    {
                        public void run()
                        {
                            Optional<ButtonType> userClickAlert = alertService.showAlert(
                                    Alert.AlertType.ERROR,
                                    "Book deletion error",
                                    "Connection to server was lost, press OK button for return to start menu",
                                    true);
                            try
                            {
                                if ((userClickAlert.get() == ButtonType.OK))
                                {
                                    setScene("Start menu", StartController.class, fxWeaver);
                                }
                            }
                            catch (NoSuchElementException e)
                            {
                                setScene("Start menu", StartController.class, fxWeaver);
                            }
                        }
                    });
                    return Mono.empty();
                })
                .block();

        id.setCellValueFactory(new PropertyValueFactory<BookDTO, Long>("id"));
        author.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("title"));
        publishDate.setCellValueFactory(new PropertyValueFactory<BookDTO, String>("publishDate"));

        libTable.getItems().addAll(booksList);

        libTable.setRowFactory(
                tableView ->
                {
                    TableRow<BookDTO> row = new TableRow<>();
                    row.setOnMouseClicked(
                            event ->
                            {
                                if (event.getClickCount() == 2 && (!row.isEmpty()))
                                {
//                                    byte[] bytesOfBook = webClient.get()
//                                            .uri(bookGetBytes + row.getItem().getId())
//                                            .retrieve()
//                                            .bodyToMono(byte[].class)
//                                            .doOnSuccess(response -> {
//
//                                                //TODO придумать как обрабатывать bytesOfBook внутри doOnSuccess
//                                                try {
//                                                    PDFDisplayer displayer = new PDFDisplayer();
//                                                    displayer.loadPDF(fileUploadService.convertToFile(bytesOfBook));
//                                                    setScene("Book", displayer);
//                                                } catch (IOException e) {
//                                                    log.error("Error when trying to load PDF to WebView {}", e.getMessage());
//                                                }
//                                            })
//                                            .doOnError(exception -> log.error("Error when trying to send a request to the server to get the specified book [{}]", exception.getMessage()))
//                                            .onErrorResume(WebClientRequestException.class, exception -> {
//                                                Platform.runLater(new Runnable() {
//                                                    public void run() {
//                                                        Optional<ButtonType> userClickAlert = alertService.showAlert(
//                                                                Alert.AlertType.ERROR,
//                                                                "Book deletion error",
//                                                                "Connection to server was lost, press OK button for return to start menu",
//                                                                true);
//                                                        try {
//                                                            if ((userClickAlert.get() == ButtonType.OK)) {
//                                                                setScene("Start menu", StartController.class, fxWeaver);
//                                                            }
//                                                        }catch (NoSuchElementException e){
//                                                            setScene("Start menu", StartController.class, fxWeaver);
//                                                        }
//                                                    }
//                                                });
//                                                return Mono.empty();
//                                            })
//                                            .block();
                                }
                            });
                    return row;
                });

        backButton.setOnAction(
                actionEvent ->
                {
                    setScene(backButton, "Main menu", MainMenuController.class, fxWeaver);
                });

        deletingButton.setOnAction(
                actionEvent ->
                {
                    DeleteDTO deleteDTO = DeleteDTO
                            .builder()
                            .id(libTable.getSelectionModel().getSelectedItem().getId())
                            .owner(userAuthRepository.getUser().getLogin())
                            .build();

                    webClient.post()
                            .uri(DELETE_BOOK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(deleteDTO)
                            .retrieve()
                            .bodyToMono(String.class)
                            .doOnSuccess(response -> libTable.getItems().removeAll(libTable.getSelectionModel().getSelectedItem()))
                            .onErrorResume(WebClientRequestException.class, exception ->
                            {
                                Platform.runLater(new Runnable() {
                                    public void run()
                                    {
                                        Optional<ButtonType> userClickAlert = alertService.showAlert(
                                                Alert.AlertType.ERROR,
                                                "Book deletion error",
                                                "Connection to server was lost, press OK button for return to start menu",
                                                true);
                                        try
                                        {
                                            if ((userClickAlert.get() == ButtonType.OK))
                                            {
                                                setScene(deletingButton, "Start menu", StartController.class, fxWeaver);
                                            }
                                        }
                                        catch (NoSuchElementException e)
                                        {
                                            setScene(deletingButton, "Start menu", StartController.class, fxWeaver);
                                        }
                                    }
                                });
                                return Mono.empty();
                            })
                            .onErrorResume(WebClientResponseException.class, exception ->
                            {
                                if (exception.getStatusCode() == HttpStatus.BAD_REQUEST)
                                {
                                    log.error("Uncorrected input data: [{}], reason [{}]",
                                            exception.getResponseBodyAsString().replaceAll("\n", " "),
                                            exception.getMessage());
                                    Platform.runLater(new Runnable()
                                    {
                                        public void run()
                                        {
                                            alertService.showAlert(Alert.AlertType.ERROR,
                                                    "Book deletion error",
                                                    "An unexpected error has occurred, the administrator has been notified and will try to fix it as soon as possible",
                                                    false);
                                            log.error("The book can't be deleted because [{}]", exception.getResponseBodyAsString().replaceAll("\n", " "));
                                            try
                                            {
                                                mailService.sendEmail(emailConfig, exception.getResponseBodyAsString(), deleteDTO.getId());
                                            }
                                            catch (MessagingException e)
                                            {
                                                log.error("An error occurred while trying to send a notification to the administrator [{}]", e);
                                            }
                                        }
                                    });
                                    return Mono.empty();
                                }
                                else if (exception.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY)
                                {
                                    log.info("Error deletion book, reason [{}]",  exception.getResponseBodyAsString().replaceAll("\n", " "));
                                    Platform.runLater(new Runnable()
                                    {
                                        public void run()
                                        {
                                            alertService.showAlert(Alert.AlertType.ERROR,
                                                    "Book owner's error",
                                                    exception.getResponseBodyAsString(),
                                                    false);
                                        }
                                    });
                                    return Mono.empty();
                                }
                                return Mono.error(exception);
                            })
                            .block();
                });
    }
}
