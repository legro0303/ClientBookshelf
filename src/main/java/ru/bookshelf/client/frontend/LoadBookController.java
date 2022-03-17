package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import ru.bookshelf.client.service.dto.BookDTO;

import java.io.*;
import java.time.LocalDate;
import java.util.Optional;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/loadBook.fxml")
public class LoadBookController extends BaseController {

    @Autowired
    private FxWeaver fxWeaver;

    @Autowired
    private Stage stage;

    @Autowired
    private WebClient webClient;

    @Autowired
    private AuthController authController;

    private final AlertService alertService;


    @FXML private Button loadBookBackButton;
    @FXML private Button loadBookButton;
    @FXML private TextField tbAuthor;
    @FXML private TextField tbTitle;
    @FXML private DatePicker dpPublishDate;
    @FXML private Button saveBookButton;

    private final String bookAdd;


    public LoadBookController(AlertService alertService, @Value("${bookshelf.book.add}") String bookAdd) {
        this.alertService = alertService;
        this.bookAdd = bookAdd;
    }
    FileChooser fileChooser = new FileChooser();
    File choosedFile;
    InputStream file;

    @FXML
    void initialize() {
        loadBookBackButton.setOnAction(
                actionEvent -> {
                        setScene(loadBookBackButton,"Библиотека", MainMenuController.class, fxWeaver);
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
                    if (tbAuthor.getText().trim().isEmpty()
                            || tbTitle.getText().trim().isEmpty()
                            || dpPublishDate.getValue() == null) {
                        alertService.showAlert(Alert.AlertType.ERROR, "Ошибка", "Вы заполнили не все поля", false);
                    } else {
                       // uploadedBook.setLogin(AuthController.user.getLogin());
                        try {
                            file = new FileInputStream(new File(choosedFile.getAbsolutePath()));
                        } catch (FileNotFoundException | NullPointerException e) {
                            alertService.showAlert(Alert.AlertType.ERROR, "Ошибка",
                                    "Пожалуйста, загрузите книгу прежде чем сохранить её в библиотеку", false);
                            e.printStackTrace();
                        }
                        try {
                              BookDTO bookDTO = BookDTO
                                        .builder()
                                        .author(tbAuthor.getText())
                                        .title(tbTitle.getText())
                                        .publishDate(LocalDate.parse(dpPublishDate.getValue().toString()))
                                        .owner("qwe") //TODO доделать получение логина пользователя
                                        .fileData(file.readAllBytes())
                                        .build();

                           webClient.post()
                                    .uri(bookAdd)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(bookDTO)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .block();
                            Optional<ButtonType> userClickAlert =  alertService.showAlert(Alert.AlertType.INFORMATION, "Успех!", "Книга успешно загружена!", true);
                            if (userClickAlert.get() == ButtonType.OK) {
                                clearFields(tbAuthor,tbTitle, dpPublishDate);
                            }
                         } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
