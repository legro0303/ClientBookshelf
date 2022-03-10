package ru.bookshelf.client.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class ValidationResultDTO {
  public Boolean loginValidated;
  public Boolean userAuthorized;
  public Boolean bookSaved;
  public long booksCount;
}
