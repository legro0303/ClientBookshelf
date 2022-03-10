package ru.bookshelf.client.domain.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class Person {
  public String firstName;
  public String secondName;
  public String login;
  public String mail;
  public String password;
}
