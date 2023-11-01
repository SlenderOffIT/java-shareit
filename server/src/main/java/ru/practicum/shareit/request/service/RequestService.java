package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestJson;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface RequestService {

    ItemRequestDto createRequest(int userId, ItemRequestJson requestJson);

    List<ItemRequestResponse> getAllRequestByUser(int userId);

    List<ItemRequestResponse> getAllRequest(int userId, int from, int size);

    ItemRequestResponse getRequestById(int userId, int requestId);
}
