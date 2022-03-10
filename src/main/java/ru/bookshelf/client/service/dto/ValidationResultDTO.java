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
  public Boolean validationLogin;
  public Boolean authorization;
  public Boolean bookSaving;
  public long booksCount;
}
