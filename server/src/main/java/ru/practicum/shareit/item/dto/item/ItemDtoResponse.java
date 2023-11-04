package ru.practicum.shareit.item.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDtoResponse {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer request;
    private BookingDtoResponse lastBooking;
    private BookingDtoResponse nextBooking;
    private List<CommentDto> comments;

    public ItemDtoResponse(int id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
