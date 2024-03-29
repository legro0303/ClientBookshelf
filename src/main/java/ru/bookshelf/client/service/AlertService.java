package ru.bookshelf.client.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    public Optional<ButtonType> showAlert(Alert.AlertType type, String title,
                                          String content, boolean NeedToReturnResult) {
        log.info("Show alert = [{}]", title);
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();

        if (NeedToReturnResult) return result;
        else
        {
            return null;
        }
    }
}

