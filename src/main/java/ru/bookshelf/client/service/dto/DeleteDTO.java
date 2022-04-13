package ru.bookshelf.client.service.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class DeleteDTO {
    long id;
    String owner;
}
