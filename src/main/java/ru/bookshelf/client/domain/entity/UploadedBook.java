package ru.bookshelf.client.domain.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class UploadedBook {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String author;
  private String title;
  private LocalDate publishDate;
  @Lob
  @Type(type = "org.hibernate.type.TextType")
  private byte[] fileData;
}
