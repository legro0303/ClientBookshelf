package ru.bookshelf.client.domain.entity;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class UploadedBook {
    private long id;
    private String author;
    private String title;
    private LocalDate publishDate;
    private byte[] fileData;
}
