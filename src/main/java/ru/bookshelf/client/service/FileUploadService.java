package ru.bookshelf.client.service;

import javafx.scene.control.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileUploadService {
    private final String uploadDir;
    private final AlertService alertService;

    public FileUploadService(@Value("${libraryserv.file-storage.upload-dir}") String uploadDir, AlertService alertService) {
        this.uploadDir = uploadDir;
        this.alertService = alertService;
    }

    public File convertToFile(byte[] book) throws IOException {
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            try {
                folder.mkdirs();
            } catch (SecurityException e) {
                log.error("Can't create output catalog because has not permission");
                alertService.showAlert(Alert.AlertType.ERROR,
                        "Нет доступа",
                        "Приложение запущено не от имени администратора, нет прав на создание файла-книги",
                        false);
            }
        }
        Path filepath = Paths.get(uploadDir + File.separator + "out.pdf");
        if (Files.exists(filepath)) {
            Files.delete(filepath);
        }
        Files.write(Files.createFile(filepath), book);
        File file = new File(String.valueOf(filepath));
        return file;
    }
}
