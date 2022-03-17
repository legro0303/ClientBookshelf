package ru.bookshelf.client.service.repository;

import org.springframework.stereotype.Repository;
import ru.bookshelf.client.service.dto.UserAuthDTO;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserAuthRepository {
    private UserAuthDTO user;

    public void addUser(UserAuthDTO user) {
        this.user = user;
    }

    public void deleteUser() {
        this.user = null;
    }

    public UserAuthDTO getUser() {
        return this.user;
    }
}
