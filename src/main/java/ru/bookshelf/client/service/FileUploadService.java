package ru.bookshelf.client.service;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadService {

  public byte[] uploadToServer(File file) {

    String strPath = file.getPath();
    Path path = Paths.get(strPath);
    String name = file.getName();
    String originalFileName = file.getName();
    ;
    String contentType = "text/plain";
    byte[] content = null;
    byte[] data = null;
    try {
      content = Files.readAllBytes(path);
    } catch (final IOException e) {
    }
    MultipartFile result = new MockMultipartFile(name, originalFileName, contentType, content);
    try {
      data = result.getBytes();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return data;
  }

  public String convertToFile(byte[] book) {
    File outFile = new File("out.pdf");
    OutputStream out = null;
    try {
      out = new FileOutputStream(outFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    try {
      out.write(book);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return outFile.getAbsolutePath();
  }
}
