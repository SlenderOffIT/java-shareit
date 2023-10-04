package ru.practicum.shareit.item.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDtoJson {
    @NotNull
    private String text;
    @NotNull
    private int itemId;
}
