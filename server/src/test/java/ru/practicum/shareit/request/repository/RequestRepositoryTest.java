package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByRequesterId() {
        User user = new User("Вася", "asdfgh@gmail.com");
        User create = userRepository.save(user);

        ItemRequest request = new ItemRequest("text");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest requestId = requestRepository.save(request);

        ItemRequest request1 = new ItemRequest("text1");
        request1.setRequester(user);
        request1.setCreated(LocalDateTime.now());
        ItemRequest requestId1 = requestRepository.save(request1);

        List<ItemRequest> requests = requestRepository.findByRequesterId(create.getId());

        assertEquals(2, requests.size());
        assertEquals(requestId, requests.get(0));
        assertEquals(requestId1, requests.get(1));
    }

    @Test
    void findAllByRequesterIdNot() {
        Sort sort = Sort.by(Sort.Order.desc("created"));
        Pageable pageable = PageRequest.of(0 / 10, 10, sort);

        User user = new User("Вася", "asdfgh@gmail.com");
        User create = userRepository.save(user);

        User user2 = new User("Петя", "dfgh@gmail.com");
        User create2 = userRepository.save(user2);

        ItemRequest request = new ItemRequest("text");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest requestId = requestRepository.save(request);

        ItemRequest request1 = new ItemRequest("text1");
        request1.setRequester(user2);
        request1.setCreated(LocalDateTime.now());
        ItemRequest requestId1 = requestRepository.save(request1);

        List<ItemRequest> requestList = requestRepository.findAllByRequesterIdNot(create.getId(), pageable);

        assertEquals(1, requestList.size());
        assertEquals(requestId1, requestList.get(0));
    }
}