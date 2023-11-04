package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private long itemId;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime start;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime end;
}