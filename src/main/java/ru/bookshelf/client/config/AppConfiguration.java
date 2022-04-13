package ru.bookshelf.client.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "libraryserv")
public class AppConfiguration {
    private EmailConfig email;

    @PostConstruct
    public void init() {
        log.debug ("------------------- AppConfiguration ----------------------");
        log.debug ("{}", email);
    }

    @Getter
    @Setter
    @ToString
    public static class EmailConfig {
        private String to;
        private String from;
        private String subjectServerUnavailable;
        private String subjectCannotDeleteBook;
        private String templateServerUnavailable;
        private String templateCannotDeleteBook;
    }
}
