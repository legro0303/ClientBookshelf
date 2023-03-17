package ru.bookshelf.client;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.bookshelf.client.frontend.StartController;

@OpenAPIDefinition(info = @Info(title = "Bookshelf service API", version = "1.0", description = "Bookshelf service Information"))
/** JavaFX App */
public class ClientApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.context = new SpringApplicationBuilder()
                .sources(StartJavaFX.class)
                .run(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(StartController.class);
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Start menu");
        primaryStage.show();
    }

    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}