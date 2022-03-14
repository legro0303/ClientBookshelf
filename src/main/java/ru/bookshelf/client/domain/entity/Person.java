package ru.bookshelf.client.domain.entity;

import lombok.*;

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
    public String password;
    public String mail;
}
