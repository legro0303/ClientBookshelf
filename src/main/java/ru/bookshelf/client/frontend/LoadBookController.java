package ru.bookshelf.client.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import ru.bookshelf.client.domain.entity.UploadedBook;
import ru.bookshelf.client.service.AlertService;
import ru.bookshelf.client.service.LoadSceneService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class LoadBookController {

    @FXML private Button loadBookBackButton;
    @FXML private Button loadBookButton;
    @FXML private TextField tbAuthor;
    @FXML private TextField tbTitle;
    @FXML private DatePicker dpPublish_date;
    @FXML private Button saveBookButton;

    FXMLLoader loader = new FXMLLoader();
    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    File choosedFile;
    InputStream file;


    @FXML
    void initialize() {
        UploadedBook uploadedBook = new UploadedBook();

        loadBookBackButton.setOnAction(
                actionEvent -> {
                    LoadSceneService loadSceneService = new LoadSceneService();
                    loadSceneService.setScene(loader, loadBookButton, "/FXML/mainMenu.fxml",
                            stage, "Библиотека");
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
//                            LibraryDTO libraryDTO = LibraryDTO
//                                    .builder()
//                                    .author(tbAuthor.getText())
//                                    .title(tbTitle.getText())
//                                    .publishDate(LocalDate.parse(dpPublish_date.getValue().toString()))
//                                    .fileData(file.readAllBytes())
//                                    .build();
//                            WebClient webClient = createWebClient(sbisDocument.getSbisSource().getCallbackUrlOutput());
//                            webClient.post()
//                                    .contentType(MediaType.APPLICATION_JSON)
//                                    .bodyValue(docStatusRequestDTO)
//                                    .retrieve()
//                                    .bodyToMono(Void.class)
//                                    .doOnSuccess(response -> sbisDocumentService.markDocumentAsSync(sbisDocument))
//                                    .onErrorMap(throwable -> {
//                                        LoggerUtil.log(()->mailService.sendSyncDocStatusErrorEmail(sbisDocument.getId(),sbisDocument.getSbisSource().getName(),sbisDocument.getSbisSource().getCallbackUrlOutput()));
//                                        return throwable;
//                                    })
//                                    .block();


//                            MultipartBody body =
//                                    Unirest.post("http://localhost:8080/library/add")
//                                            .header(HttpHeaders.CONTENT_TYPE, "multipart/form-data")
//                                            .field("author", tbAuthor.getText())
//                                            .field("title", tbTitle.getText())
//                                            .field("publishDate", LocalDate.parse(dpPublish_date.getValue().toString()))
//                                            .field("login", "qwe")
//                                            .field("fileData", file);
//                            HttpResponse<String> file = body.asString();
//                            System.out.println(file.getStatus());

//                            switch (file.getStatus()) {
//                                case (500):
//                                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                                    alert.setTitle("Ошибка");
//                                    alert.setContentText(
//                                            "Вы пытаетесь загрузить слишком большую книгу. Допустимый размер - 1048576 байт");
//                                    alert.setHeaderText(null);
//                                    alert.showAndWait();
//                                    break;
//                                case (200):
//                                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
//                                    successAlert.setTitle("Успех!");
//                                    successAlert.setContentText("Книга успешно загружена!");
//                                    tbAuthor.clear();
//                                    tbTitle.clear();
//                                    successAlert.setHeaderText(null);
//                                    successAlert.showAndWait();
//                                    break;
//                                default:
//                                    break;
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
