package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.dto.LibraryDTO;

import java.io.*;
import java.time.LocalDate;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/loadBook.fxml")
public class LoadBookController extends BaseController {

    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private Stage stage;

    @FXML private Button loadBookBackButton;
    @FXML private Button loadBookButton;
    @FXML private TextField tbAuthor;
    @FXML private TextField tbTitle;
    @FXML private DatePicker dpPublish_date;
    @FXML private Button saveBookButton;

    private final String bookUrl;
    public LoadBookController(@Value("${bookshelf.add-book-url}") String bookUrl) {
        this.bookUrl = bookUrl;
    }


    FileChooser fileChooser = new FileChooser();
    File choosedFile;
    InputStream file;

    @FXML
    void initialize() {
        loadBookBackButton.setOnAction(
                actionEvent -> {
                    try {
                        setScene(loadBookBackButton,"Библиотека", MainMenuController.class, fxWeaver);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        loadBookButton.setOnAction(
                actionEvent -> {
                    FileChooser.ExtensionFilter extFilter =
                            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                    fileChooser.getExtensionFilters().add(extFilter);
                    fileChooser.setTitle("Библиотека");
                    choosedFile = fileChooser.showOpenDialog(stage);
                });
        saveBookButton.setOnAction(
                actionEvent -> {
                    AlertService alertService = new AlertService();
                    if (tbAuthor.getText().trim().isEmpty()
                            || tbTitle.getText().trim().isEmpty()
                            || dpPublish_date.getValue() == null) {
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "Вы заполнили не все поля", false);
                    } else {
//                        uploadedBook.setLogin(AuthController.user.getLogin());
                        try {
                            file = new FileInputStream(new File(choosedFile.getAbsolutePath()));
                        } catch (FileNotFoundException | NullPointerException e) {
                            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка",
                                    "Пожалуйста, загрузите книгу прежде чем сохранить её в библиотеку", false);
                            e.printStackTrace();
                        }
                        try {
                            LibraryDTO libraryDTO = null;
                            try {
                                libraryDTO = LibraryDTO
                                        .builder()
                                        .author(tbAuthor.getText())
                                        .title(tbTitle.getText())
                                        .publishDate(LocalDate.parse(dpPublish_date.getValue().toString()))
                                        .login("qwe") //TODO доделать получение логина пользователя
                                        .fileData(file.readAllBytes())
                                        .build();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            WebClient webClient = WebClient.builder().baseUrl(bookUrl).build();

                            webClient.post()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(libraryDTO)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .doOnSuccess(response -> alertService.showAlert(Alert.AlertType.INFORMATION, "Успех!", "Книга успешно загружена!", false))
                                    .onErrorMap(throwable -> {
                                        return throwable;
                                    })
                                    .block();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
