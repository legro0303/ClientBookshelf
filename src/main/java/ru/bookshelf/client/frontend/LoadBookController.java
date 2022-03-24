package ru.bookshelf.client.frontend;

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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.dto.BookDTO;
import ru.bookshelf.client.service.repository.UserAuthRepository;

import java.io.*;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@FxmlView("/FXML/loadingBook.fxml")
public class LoadBookController extends BaseController {
    @Autowired private Stage stage;
    @Autowired private FxWeaver fxWeaver;
    @Autowired private WebClient webClient;

    @FXML private TextField titleTf;
    @FXML private TextField authorTf;
    @FXML private Button backButton;
    @FXML private Button savingBookButton;
    @FXML private Button loadingBookButton;
    @FXML private DatePicker publishDateDp;

    private final String bookAdd;
    private final AlertService alertService;
    private final UserAuthRepository userAuthRepository;

    private File choosedFile;
    private InputStream file;

    public LoadBookController(@Value("${bookshelf.book.add}") String bookAdd, AlertService alertService, UserAuthRepository userAuthRepository) {
        this.bookAdd = bookAdd;
        this.alertService = alertService;
        this.userAuthRepository = userAuthRepository;
    }

    @FXML
    void initialize() {
        backButton.setOnAction(
                actionEvent -> {
                    setScene(backButton, "Библиотека", MainMenuController.class, fxWeaver);
                });

        loadingBookButton.setOnAction(
                actionEvent -> {
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter =
                            new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
                    fileChooser.getExtensionFilters().add(extFilter);
                    fileChooser.setTitle("Библиотека");
                    choosedFile = fileChooser.showOpenDialog(stage);
                });

        savingBookButton.setOnAction(
                actionEvent -> {
                    if (authorTf.getText().trim().isEmpty()
                            || titleTf.getText().trim().isEmpty()
                            || publishDateDp.getValue() == null) {
                        alertService.showAlert(Alert.AlertType.ERROR, "Пустые поля при загрузке книги", "Вы заполнили не все поля", false);
                    } else {
                        try {
                            file = new FileInputStream(new File(choosedFile.getAbsolutePath()));
                        } catch (FileNotFoundException | NullPointerException e) {
                            alertService.showAlert(Alert.AlertType.ERROR, "Книга не загружена",
                                    "Пожалуйста, загрузите книгу прежде чем сохранить её в библиотеку", false);
                            log.error("Загружаемый файл не найден или не выбран пользователем при загрузке - [{}]", e.getMessage());
                        }
                        try {
                            BookDTO bookDTO = BookDTO
                                    .builder()
                                    .author(authorTf.getText())
                                    .title(titleTf.getText())
                                    .publishDate(LocalDate.parse(publishDateDp.getValue().toString()))
                                    .owner(userAuthRepository.getUser().getLogin())
                                    .fileData(file.readAllBytes())
                                    .build();
                            webClient.post()
                                    .uri(bookAdd)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(bookDTO)
                                    .retrieve()
                                    .bodyToMono(Void.class)
                                    .doOnError(exception -> log.error("Ошибка при попытке отправить запрос серверу для загрузки книги - [{}]", exception.getMessage()))
                                    .block();
                        } catch (IOException e) {
                            log.error("Ошибка при чтении книги из директории [{}]", e);
                        }
                        Optional<ButtonType> userClickAlert = alertService.showAlert(Alert.AlertType.INFORMATION, "Книга загружена!", "Книга успешно загружена!", true);
                        if (userClickAlert.get() == ButtonType.OK) {
                            clearFields(authorTf, titleTf, publishDateDp);
                        }
                    }
                });
    }
}
