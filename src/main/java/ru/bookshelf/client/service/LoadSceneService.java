package ru.bookshelf.client.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LoadSceneService {
    public void setScene(FXMLLoader loader, Button button, String scene, Stage stage, String title){
        button.getScene().getWindow().hide();
        loader.setLocation(getClass().getResource(scene));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
    public void setScene(FXMLLoader loader, Hyperlink link, String scene, Stage stage, String title){
        link.getScene().getWindow().hide();
        loader.setLocation(getClass().getResource(scene));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
    public void clearFields(PasswordField passReg, TextField loginReg, TextField secondNameReg, TextField nameReg, TextField mailReg){
        passReg.clear();
        loginReg.clear();
        secondNameReg.clear();
        nameReg.clear();
        mailReg.clear();
    }
}
