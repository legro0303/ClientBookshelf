package ru.bookshelf.client.service.dto;

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
    String title;
    String author;
    LocalDate publishDate;
    String owner;
    byte[] fileData;
}
