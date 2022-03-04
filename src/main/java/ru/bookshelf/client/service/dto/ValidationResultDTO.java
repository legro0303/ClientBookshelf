package ru.bookshelf.client.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResultDTO {
  public Boolean validationLogin;
  public Boolean authorization;
  public Boolean bookSaving;
  public long booksCount;
}
