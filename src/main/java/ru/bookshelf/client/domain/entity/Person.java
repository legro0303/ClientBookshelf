package ru.bookshelf.client.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
  private String s_name;
  private String f_name;
  private String login;
  private String mail;
  private String password;
}
