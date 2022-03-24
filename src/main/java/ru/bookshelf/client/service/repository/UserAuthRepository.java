package ru.bookshelf.client.service.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.bookshelf.client.service.dto.UserAuthDTO;

@Slf4j
@Repository
public class UserAuthRepository {
    private UserAuthDTO user;

    public void addUser(UserAuthDTO user) {
        log.info("Adding authorized user [{}] to local memory ", user.getLogin());
        this.user = user;
    }

    public void deleteUser() {
        log.info("Deleting authorized user [{}] from local memory ", user.getLogin());
        this.user = null;
    }

    public UserAuthDTO getUser() {
        return this.user;
    }
}
