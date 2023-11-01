package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestJson;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private RequestService requestService;

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader(value = SHARER_USER) int idUser,
                                      @RequestBody ItemRequestJson requestJson) {
        log.debug("Поступил запрос на добавление запроса вещи с названием.");
        return requestService.createRequest(idUser, requestJson);
    }

    @GetMapping
    public List<ItemRequestResponse> getAllRequestByUser(@RequestHeader(value = SHARER_USER) int idUser) {
        log.debug("Поступил запрос на просмотр всех своих запросов от пользователя с id {}.", idUser);
        return requestService.getAllRequestByUser(idUser);
    }

    @GetMapping("/all")
    public List<ItemRequestResponse> getAllRequest(@RequestHeader(value = SHARER_USER) int idUser,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.debug("Поступил запрос на просмотр всех запросов от пользователя с id {}.", idUser);
        return requestService.getAllRequest(idUser, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponse getRequestById(@RequestHeader(value = SHARER_USER) int idUser, @PathVariable int requestId) {
        log.debug("Поступил запрос на просмотр запроса с id {}.", requestId);
        return requestService.getRequestById(idUser, requestId);
    }
}
