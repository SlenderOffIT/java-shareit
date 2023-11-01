package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoJson;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoResponse;

import java.util.List;

@Service
public interface ItemService {

    List<ItemDtoResponse> getAllItems(int idUser);

    ItemDtoResponse getItemById(int idItem, int idUser);

    ItemDto postItem(ItemDto itemDto, int idUser);

    ItemDto update(ItemDto itemDto, int idItem, int idUser);

    void delete(int idItem, int idUser);

    List<ItemDto> search(String text);

    CommentDto postComment(CommentDtoJson comment, int idUser, int itemId);
}
