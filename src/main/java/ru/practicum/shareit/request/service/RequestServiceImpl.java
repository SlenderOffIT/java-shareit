package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestItemBadRequest;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.MapperItemDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.MapperRequestDto.*;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_USER;

@Service
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(int userId, ItemRequestJson requestJson) {
        log.debug("Обрабатываем запрос на создание запроса предмета.");
        if (requestJson.getDescription() == null || requestJson.getDescription().isEmpty()) {
            log.warn("Описание запроса не может быть пустым.");
            throw new RequestItemBadRequest("Описание запроса не может быть пустым.");
        }

        ItemRequest request = toRequest(requestJson);
        User user = exceptionIfNotUser(userId);

        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestResponse> getAllRequestByUser(int userId) {
        log.debug("Обрабатываем запрос на просмотр всех своих запросов от пользователя с id {}.", userId);
        exceptionIfNotUser(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterId(userId);

        return combiningRequestItem(requests);
    }


    @Override
    public List<ItemRequestResponse> getAllRequest(int userId, int from, int size) {
        log.debug("Обрабатываем запрос на просмотр всех запросов от пользователя с id {}.", userId);
        Sort sort = Sort.by(Sort.Order.desc("created"));
        Pageable pageable = PageRequest.of(from, size, sort);

        exceptionIfNotUser(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequesterIdNot(userId, pageable);

        return combiningRequestItem(requests);
    }

    @Override
    public ItemRequestResponse getRequestById(int userId, int requestId) {
        log.debug("Обрабатываем запрос на просмотр запроса с id {}.", requestId);
        exceptionIfNotUser(userId);

        ItemRequestResponse itemRequest = toRequestResponse(requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запроса с таким id {} не существует.", requestId);
                    return new RequestNotFoundException(String.format("Запроса с таким id %d не существует.", requestId));
                }));

        itemRequest.setItems(itemRepository.findAllByRequestId(requestId).stream()
                .map(MapperItemDto::toItemDto)
                .collect(Collectors.toList()));

        return itemRequest;
    }

    private List<ItemRequestResponse> combiningRequestItem(List<ItemRequest> requests) {
        List<Integer> requestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(requestsId);
        Map<Integer, List<Item>> mapItem = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    ItemRequestResponse requestResponse = toRequestResponse(request);

                    List<ItemDto> itemList = mapItem.getOrDefault(request.getId(), Collections.emptyList()).stream()
                            .map(MapperItemDto::toItemDto)
                            .collect(Collectors.toList());

                    requestResponse.setItems(itemList);
                    return requestResponse;
                })
                .collect(Collectors.toList());
    }

    private User exceptionIfNotUser(int idUser) {
        return userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_USER.getValue(), idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });
    }
}
