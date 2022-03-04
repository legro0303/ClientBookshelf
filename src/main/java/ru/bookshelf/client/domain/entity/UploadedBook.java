package ru.bookshelf.client.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedBook {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String author;
  private String title;
  private String publish_date;
  @Lob
  @Type(type = "org.hibernate.type.TextType")
  private byte[] file_data;
}
