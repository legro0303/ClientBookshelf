package ru.bookshelf.client.service.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class LibraryDTO {
    String author;
    String title;
    LocalDate publishDate;
    String login;
    byte[] fileData;
}
