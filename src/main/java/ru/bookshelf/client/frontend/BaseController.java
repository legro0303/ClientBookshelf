package ru.bookshelf.client.frontend;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class BaseController {
    @Autowired private Stage stage;

    public void setScene(Button button, String title, Class controller, FxWeaver fxWeaver) {
        log.info("Upload scene [{}]", controller.getSimpleName());

        try
        {
            button.getScene().getWindow().hide();
            Parent root = (Parent) fxWeaver.loadView(controller);
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        }
        catch (Exception e)
        {
            log.error("Unexpected error while uploading scene " + e);
        }
    }

    public void setScene(Hyperlink link, String title, Class controller, FxWeaver fxWeaver) {
        log.info("Upload scene [{}]", controller.getSimpleName());

        try
        {
            link.getScene().getWindow().hide();
            Parent root = (Parent) fxWeaver.loadView(controller);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            log.error("Unexpected error while uploading scene " + e);
        }
    }

    public void setScene(String title, Class controller, FxWeaver fxWeaver) {
        log.info("Upload scene [{}]", controller.getSimpleName());

        try
        {
            Parent root = (Parent) fxWeaver.loadView(controller);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            log.error("Unexpected error while uploading scene " + e);
        }
    }

    public void setScene(String title, PDFDisplayer displayer) {
        log.info("Upload scene [{}]", title);

        try
        {
            Stage stage = new Stage();
            Parent root = displayer.toNode();
            stage.setTitle(title);
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            log.error("Unexpected error while uploading scene " + e);
        }
    }

    public void clearFields(TextField firstNameReg, TextField secondNameReg,
                            TextField loginReg, PasswordField passReg, TextField mailReg) {
        firstNameReg.clear();
        secondNameReg.clear();
        loginReg.clear();
        passReg.clear();
        mailReg.clear();
    }

    public void clearFields(TextField author, TextField title, DatePicker publishDate) {
        author.clear();
        title.clear();
        publishDate.getEditor().clear();
    }

    public void clearFields(TextField login, PasswordField password) {
        login.clear();
        password.clear();
    }
}
