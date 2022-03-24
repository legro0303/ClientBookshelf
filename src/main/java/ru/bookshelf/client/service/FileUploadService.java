package ru.bookshelf.client.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileUploadService {
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
