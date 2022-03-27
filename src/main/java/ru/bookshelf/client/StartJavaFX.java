package ru.bookshelf.client;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.application.Application;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.reactive.function.client.WebClient;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

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
    public PDFDisplayer getPDF() {
        return new PDFDisplayer();
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