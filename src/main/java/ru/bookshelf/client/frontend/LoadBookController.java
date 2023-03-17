package ru.bookshelf.client.frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.dto.BookDTO;
import ru.bookshelf.client.service.repository.UserAuthRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/loadingBook.fxml")
public class LoadBookController extends BaseController
{
    @Autowired
    private Stage stage;
    @Autowired
    private FxWeaver fxWeaver;
    @Autowired
    private WebClient webClient;

    @FXML
    private TextField titleTf;
    @FXML
    private TextField authorTf;
    @FXML
    private Button backButton;
    @FXML
    private Button savingBookButton;
    @FXML
    private Button loadingBookButton;
    @FXML
    private DatePicker publishDateDp;

    private final String addBook;
    private final AlertService alertService;
    private final UserAuthRepository userAuthRepository;

    private File choosedFile;
    private InputStream file;

    public LoadBookController(@Value("${libraryserv.book.add}") String addBook, AlertService alertService,
                              UserAuthRepository userAuthRepository)
    {
        this.addBook = addBook;
        this.alertService = alertService;
        this.userAuthRepository = userAuthRepository;
    }

    @FXML
    void initialize()
    {
        backButton.setOnAction(
                actionEvent ->
                {
                    setScene(backButton, "Library", MainMenuController.class, fxWeaver);
                });

        loadingBookButton.setOnAction(
                actionEvent ->
                {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter =
                            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                    fileChooser.getExtensionFilters().add(extFilter);
                    fileChooser.setTitle("Book chooser");
                    choosedFile = fileChooser.showOpenDialog(stage);
                });

        savingBookButton.setOnAction(
                actionEvent ->
                {
//                    if (authorTf.getText().trim().isEmpty()
//                            || titleTf.getText().trim().isEmpty()
//                            || publishDateDp.getValue() == null) {
//                        alertService.showAlert(Alert.AlertType.ERROR, "Пустые поля при загрузке книги", "Вы заполнили не все поля", false);
//                    } else {

                    try
                    {
                        file = new FileInputStream(new File(choosedFile.getAbsolutePath()));
                    }
                    catch (FileNotFoundException | NullPointerException e)
                    {
                        alertService.showAlert(Alert.AlertType.ERROR,
                                "The book isn't loaded",
                                "Please upload the book before saving it first",
                                false);
                        log.error("Uploading file not found or not selected by user [{}]", e.getMessage());
                    }
                    try
                    {
                        byte[] bookBytes = file.readAllBytes();
                        if (bookBytes.length >= 262144) {
                            alertService.showAlert(Alert.AlertType.ERROR,
                                    "Error book size",
                                    "The uploaded file exceeds the specified limit (262 KB)",
                                    false);
                            file.close();
                        }
                        else
                        {
                            try
                            {
                            BookDTO bookDTO = BookDTO
                                    .builder()
                                    .author(authorTf.getText())
                                    .title(titleTf.getText())
                                    .publishDate(LocalDate.parse(publishDateDp.getValue().toString()))
                                    .owner(userAuthRepository.getUser().getLogin())
                                    .fileData(bookBytes)
                                    .build();
                            webClient.post()
                                    .uri(addBook)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(bookDTO)
                                    .retrieve()
                                    .bodyToMono(String.class)
                                    .doOnSuccess(response ->
                                    {
                                        log.info("Book with title [{}] was saved successfully!", bookDTO.getTitle());
                                        Platform.runLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Optional<ButtonType> userClickAlert = alertService.showAlert(
                                                        Alert.AlertType.INFORMATION,
                                                        "Success",
                                                        "The book has been successfully uploaded!",
                                                        true);
                                                try
                                                {
                                                    if ((userClickAlert.get() == ButtonType.OK))
                                                    {
                                                        clearFields(authorTf, titleTf, publishDateDp);
                                                    }
                                                }
                                                catch (NoSuchElementException e)
                                                {
                                                    clearFields(authorTf, titleTf, publishDateDp);
                                                }
                                            }
                                        });
                                    })
                                    .onErrorResume(WebClientRequestException.class, exception ->
                                    {
                                        log.error("Connection to server was lost when user tried to load book with title [{}]", bookDTO.getTitle());
                                        Platform.runLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Optional<ButtonType> userClickAlert = alertService.showAlert(
                                                        Alert.AlertType.ERROR,
                                                        "Error connection",
                                                        "Connection to server was lost, press OK button for return to start menu",
                                                        true);
                                                try
                                                {
                                                    if ((userClickAlert.get() == ButtonType.OK))
                                                    {
                                                        setScene(savingBookButton, "Start menu", StartController.class, fxWeaver);
                                                    }
                                                }
                                                catch (NoSuchElementException e)
                                                {
                                                    setScene(savingBookButton, "Start menu", StartController.class, fxWeaver);
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
                                                            "Uncorrected input data",
                                                            exception.getResponseBodyAsString(),
                                                            false);
                                                }
                                            });
                                            return Mono.empty();
                                        }
                                        return Mono.error(exception);
                                    })
                                    .block();
                        }
                            catch (NullPointerException nullPointerException)
                            {
                            log.error("DatePicker is null [{}]", nullPointerException);
                            }
                        }
                    }
                    catch (IOException ioException)
                    {
                        log.error("Error reading bytes from file  [{}]", ioException);
                    }
                });
    }
}
