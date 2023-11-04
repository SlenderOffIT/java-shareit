package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void emailDuplicate() {
        User user = new User("Вася", "asdfgh@gmail.com");
        userRepository.save(user);
        User user2 = new User("Коля", "asdfgh@gmail.com");
        boolean falseEmail = userRepository.emailIsExist(user2.getEmail());

        assertTrue(falseEmail);
    }

    @Test
    public void emailNotDuplicate() {
        User user = new User("Вася", "asdfgh@gmail.com");
        userRepository.save(user);
        User user2 = new User("Коля", "adfgh@gmail.com");
        boolean falseEmail = userRepository.emailIsExist(user2.getEmail());

        assertFalse(falseEmail);
    }
}