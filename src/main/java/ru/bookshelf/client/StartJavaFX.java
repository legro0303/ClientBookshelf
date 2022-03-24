package ru.bookshelf.client;

import javafx.application.Application;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.WebClient;

//This main-class needs to start application using JavaFX dependencies
@SpringBootApplication
public class StartJavaFX {
    public static void main(String[] args) {
        Application.launch(ClientApplication.class, args);
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }

    @Lazy
    @Bean
    public Stage getStage() {
        return new Stage();
    }

    @Lazy
    @Bean
    public WebClient createWebClient() {
        return WebClient.create();
    }
}