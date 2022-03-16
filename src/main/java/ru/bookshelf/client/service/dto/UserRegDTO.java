package ru.bookshelf.client.service.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true) //Need to use builder with fields from superclass
public class UserRegDTO extends UserAuthDTO {
    public String firstName;
    public String secondName;
    public String mail;
}
