package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User user = new User("Вася", "asdfgh@gmail.com");
    private User user1 = new User("Игорь", "dfgh@gmail.com");
    private Item item = new Item("item", "description", true, user);
    private Item item1 = new Item("item1", "description1", true, user);

    @Test
    public void findAllItemOwner() {
        userRepository.save(user);
        Item itemCreated = itemRepository.save(item);
        Item itemCreated1 = itemRepository.save(item1);

        List<Item> itemList = itemRepository.findAllByOwnerId(user.getId());

        assertEquals(2, itemList.size());
        assertEquals(itemCreated, itemList.get(0));
        assertEquals(itemCreated1, itemList.get(1));
    }

    @Test
    public void findSearch() {
        userRepository.save(user);
        itemRepository.save(item);
        itemRepository.save(item1);

        List<Item> searchItem = itemRepository.search("1");
        assertEquals(1, searchItem.size());
        assertEquals(item1, searchItem.get(0));

        List<Item> searchItem2 = itemRepository.search("it");
        assertEquals(2, searchItem2.size());
        assertEquals(item, searchItem2.get(0));
    }

    @Test
    public void findListRequestItem() {
        userRepository.save(user);
        userRepository.save(user1);

        ItemRequest request = new ItemRequest("text");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest requestId = requestRepository.save(request);

        ItemRequest request1 = new ItemRequest("text1");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now());
        ItemRequest requestId1 = requestRepository.save(request1);

        item1.setRequest(requestId);
        Item itemCreated1 = itemRepository.save(item1);

        List<Integer> requestsId = new ArrayList<>();
        requestsId.add(requestId.getId());
        requestsId.add(requestId1.getId());
        List<Item> itemList = itemRepository.findAllByRequestIdIn(requestsId);

        assertEquals(1, itemList.size());
        assertEquals(itemCreated1, itemList.get(0));

        item.setRequest(requestId1);
        Item itemCreated = itemRepository.save(item);

        itemList = itemRepository.findAllByRequestIdIn(requestsId);
        assertEquals(2, itemList.size());
        assertEquals(itemCreated1, itemList.get(0));
        assertEquals(itemCreated, itemList.get(1));
    }

    @Test
    public void findRequestItem() {
        userRepository.save(user);
        userRepository.save(user1);

        ItemRequest request = new ItemRequest("text");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        ItemRequest requestId = requestRepository.save(request);

        ItemRequest request1 = new ItemRequest("text1");
        request1.setRequester(user1);
        request1.setCreated(LocalDateTime.now());
        requestRepository.save(request1);

        item1.setRequest(requestId);
        Item itemCreated1 = itemRepository.save(item1);

        List<Item> items = itemRepository.findAllByRequestId(requestId.getId());

        assertEquals(1, items.size());
        assertEquals(itemCreated1, items.get(0));
    }
}