package ru.bookshelf.client.service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true) //Need to use builder from subclasses using login and password fields
public class UserAuthDTO {
    public String login;
    public String password;
}
