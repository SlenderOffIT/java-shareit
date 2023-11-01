package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestJson;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(value = SHARER_USER) long idUser,
                                              @RequestBody @Valid ItemRequestJson requestJson) {
        log.info("Post request user {}", idUser);
        return requestClient.postRequest(requestJson, idUser);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestByUser(@RequestHeader(value = SHARER_USER) long idUser) {
        log.info("Get request user {}", idUser);
        return requestClient.getAllRequestByUser(idUser);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(value = SHARER_USER) long idUser,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") long from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") long size) {
        log.info("Get request user {}, from {}, size {}", idUser, from, size);
        return requestClient.getAllRequest(idUser, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(value = SHARER_USER) long idUser, @PathVariable long requestId) {
        log.info("Get request {} user {}", requestId, idUser);
        return requestClient.getRequestById(idUser, requestId);
    }
}
