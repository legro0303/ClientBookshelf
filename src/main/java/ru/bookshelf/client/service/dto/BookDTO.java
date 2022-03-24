package ru.bookshelf.client.service.dto;

import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class BookDTO {
    long id;
    String author;
    String title;
    LocalDate publishDate;
    String owner;
    byte[] fileData;
}
