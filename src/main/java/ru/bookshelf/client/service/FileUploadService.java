package ru.bookshelf.client.service;

import java.io.File;

public interface FileUploadService {
    public byte[] uploadToServer(File file);

    public String convertToFile(byte[] book);
}
