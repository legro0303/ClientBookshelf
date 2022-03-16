package ru.bookshelf.client.frontend;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {
    @Autowired
    private Stage stage;

    public void setScene(Button button, String title, Class controller, FxWeaver fxWeaver) {
        button.getScene().getWindow().hide();
        Parent root = (Parent) fxWeaver.loadView(controller);
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.setResizable(false);
        stage.show();

    }

    public void setScene(Hyperlink link, String title, Class controller, FxWeaver fxWeaver) {
        link.getScene().getWindow().hide();
        Parent root = (Parent) fxWeaver.loadView(controller);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();

    }

    public void clearFields(TextField firstNameReg, TextField secondNameReg, TextField loginReg, PasswordField passReg, TextField mailReg) {
        firstNameReg.clear();
        secondNameReg.clear();
        loginReg.clear();
        passReg.clear();
        mailReg.clear();
    }

    public void clearFields(TextField login, PasswordField password) {
        login.clear();
        password.clear();
    }
}
