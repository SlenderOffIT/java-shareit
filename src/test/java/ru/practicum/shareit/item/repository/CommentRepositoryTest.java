package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    User user;
    User user2;
    User user3;
    Item item;
    Item item2;
    Comment comment;
    Comment comment1;
    Comment comment2;

    @BeforeEach
    public void before() {

        user = new User("Вася", "asdfgh@gmail.com");
        user2 = new User("Петя", "dfgh@gmail.com");
        user3 = new User("Ваня", "gggeeef@mail.ru");
        item = new Item("item", "description", false, user);
        item2 = new Item("item1", "description1", true, user);
        comment = new Comment("comment");
        comment1 = new Comment("comment1");
        comment2 = new Comment("comment2");
    }

    @Test
    void findByItemId() {
        User createUser = userRepository.save(user);
        User createUser2 = userRepository.save(user2);
        Item itemCreated = itemRepository.save(item);
        Item itemCreated1 = itemRepository.save(item2);

        comment.setItem(itemCreated);
        comment.setAuthor(createUser);
        comment.setCreated(LocalDateTime.now());
        Comment commentCreated = commentRepository.save(comment);

        comment1.setItem(itemCreated);
        comment1.setAuthor(createUser2);
        comment1.setCreated(LocalDateTime.now());
        Comment commentCreated2 = commentRepository.save(comment1);

        List<Comment> commentList = commentRepository.findByItemId(itemCreated.getId());

        assertEquals(2, commentList.size());
        assertEquals(commentCreated, commentList.get(0));
        assertEquals(commentCreated2, commentList.get(1));
    }

    @Test
    void findByItemIdIn() {
        User createUser = userRepository.save(user);
        User createUser2 = userRepository.save(user2);
        Item itemCreated = itemRepository.save(item);
        Item itemCreated2 = itemRepository.save(item2);

        comment.setItem(itemCreated);
        comment.setAuthor(createUser);
        comment.setCreated(LocalDateTime.now());
        Comment commentCreated = commentRepository.save(comment);

        comment1.setItem(itemCreated);
        comment1.setAuthor(createUser2);
        comment1.setCreated(LocalDateTime.now());
        Comment commentCreated2 = commentRepository.save(comment1);

        comment2.setItem(itemCreated2);
        comment2.setAuthor(createUser2);
        comment2.setCreated(LocalDateTime.now());
        Comment commentCreated3 = commentRepository.save(comment2);

        List<Integer> idComments = Arrays.asList(itemCreated.getId(), itemCreated2.getId());

        List<Comment> commentList = commentRepository.findByItemIdIn(idComments);

        assertEquals(3, commentList.size());
        assertEquals(commentCreated, commentList.get(0));
        assertEquals(commentCreated2, commentList.get(1));
        assertEquals(commentCreated3, commentList.get(2));
    }
}