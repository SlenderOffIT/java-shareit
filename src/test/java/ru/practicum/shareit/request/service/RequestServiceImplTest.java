package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.RequestItemBadRequest;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestJson;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void before() {
        requestService = new RequestServiceImpl(requestRepository, itemRepository, userRepository);
    }

    @Test
    public void createRequestEmptyDescription() {
        ItemRequestJson requestJson = new ItemRequestJson("");

        assertThrows(RequestItemBadRequest.class, () -> {
            requestService.createRequest(1, requestJson);
        });
    }

    @Test
    public void createRequestNullUser() {
        ItemRequestJson requestJson = new ItemRequestJson("nozh");
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            requestService.createRequest(1, requestJson);
        });
    }

    @Test
    public void createRequestGood() {
        ItemRequestJson requestJson = new ItemRequestJson("nozh");
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        ItemRequest request = new ItemRequest(1, "nozh", user, LocalDateTime.now());
        ItemRequestDto requestDto = new ItemRequestDto(1, "nozh", 1, LocalDateTime.now());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto itemRequestDtoResponse = requestService.createRequest(1, requestJson);

        assertNotNull(itemRequestDtoResponse);
        assertEquals(itemRequestDtoResponse.getRequestor(), request.getRequester().getId());
    }

    @Test
    public void getAllRequestByUserNullUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            requestService.getAllRequestByUser(1);
        });
    }

    @Test
    public void getAllRequestByUserGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        ItemRequest request = new ItemRequest(1, "nozh", user, LocalDateTime.now());
        ItemRequest request1 = new ItemRequest(2, "pila dvurychka", user, LocalDateTime.now());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequesterId(anyInt())).thenReturn(Arrays.asList(request, request1));

        List<ItemRequestResponse> requestDtoList = requestService.getAllRequestByUser(1);

        assertEquals(2, requestDtoList.size());
        assertEquals("nozh", requestDtoList.get(0).getDescription());
        assertEquals("pila dvurychka", requestDtoList.get(1).getDescription());
    }

    @Test
    public void getAllRequestNullUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            requestService.getAllRequest(1, 0,1);
        });
    }

    @Test
    public void getAllRequestGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemRequest request = new ItemRequest(1, "nozh", user, LocalDateTime.now());
        ItemRequest request1 = new ItemRequest(2, "pila dvurychka", user, LocalDateTime.now());

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterIdNot(anyInt(), any(Pageable.class))).thenReturn(Arrays.asList(request, request1));
        List<ItemRequestResponse> itemRequestResponses = requestService.getAllRequest(1, 0, 1);

        assertNotNull(itemRequestResponses);
        assertEquals("nozh", itemRequestResponses.get(0).getDescription());
        assertEquals("pila dvurychka", itemRequestResponses.get(1).getDescription());
    }

    @Test
    public void getRequestByIdNullUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            requestService.getRequestById(1, 1);
        });
    }

    @Test
    public void getRequestByIdNullRequest() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RequestNotFoundException.class, () -> {
            requestService.getRequestById(1, 1);
        });
    }

    @Test
    public void getRequestByIdGood() {
        User user = new User(1, "Вася", "asdfgh@gmail.com");
        ItemRequest request = new ItemRequest(1, "nozh", user, LocalDateTime.now());
        Item item = new Item(1, "item", "description", true, user, request);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(Arrays.asList(item));

        ItemRequestResponse itemRequest = requestService.getRequestById(1, 1);

        assertEquals("nozh", itemRequest.getDescription());
        assertEquals(item.getName(), itemRequest.getItems().get(0).getName());
    }
}