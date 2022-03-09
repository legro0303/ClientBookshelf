package ru.bookshelf.client;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@OpenAPIDefinition(info = @Info(title = "Bookshelf service API", version = "1.0", description = "Bookshelf service Information"))
@EnableJpaRepositories
/** JavaFX App */
public class ClientApplication extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(ClientApplication.class.getResource("/FXML/start.fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}