package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatusEnum;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.MapperBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingBadRequest;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentDtoJson;
import ru.practicum.shareit.item.dto.comment.MapperCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDtoResponse;
import ru.practicum.shareit.item.dto.item.MapperItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.MapperBookingDto.toBookingDtoResponse;
import static ru.practicum.shareit.item.dto.comment.MapperCommentDto.toComment;
import static ru.practicum.shareit.item.dto.comment.MapperCommentDto.toCommentDto;
import static ru.practicum.shareit.item.dto.item.MapperItemDto.toItem;
import static ru.practicum.shareit.item.dto.item.MapperItemDto.toItemDto;
import static ru.practicum.shareit.item.dto.item.MapperItemDto.toItemResponse;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_ITEM;
import static ru.practicum.shareit.util.Constant.NOT_FOUND_USER;
import static ru.practicum.shareit.util.Validation.validate;

@Slf4j
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ItemDtoResponse> getAllItems(int idUser) {
        log.debug("Обрабатываем запрос на просмотр списка всех предметов пользователя с id {}.", idUser);

        List<Item> items = itemRepository.findAllByOwnerId(idUser);
        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findBookingsByItemIds(itemIds);

        List<Comment> comments = commentRepository.findByItemIdIn(itemIds);

        Map<Integer, List<Booking>> bookingMap = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Integer, List<Comment>> commentMap = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemDtoResponse itemDtoResponse = toItemResponse(item);

                    List<Booking> itemBookings = bookingMap.getOrDefault(item.getId(), Collections.emptyList());
                    List<Comment> itemComments = commentMap.getOrDefault(item.getId(), Collections.emptyList());

                    BookingDtoResponse lastBooking = itemBookings.stream()
                            .map(MapperBookingDto::toBookingDtoResponse)
                            .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                            .max(Comparator.comparing(BookingDtoResponse::getStart))
                            .orElse(null);

                    BookingDtoResponse nextBooking = itemBookings.stream()
                            .map(MapperBookingDto::toBookingDtoResponse)
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .min(Comparator.comparing(BookingDtoResponse::getStart))
                            .orElse(null);

                    List<CommentDto> commentDto = itemComments.stream()
                            .map(MapperCommentDto::toCommentDto)
                            .collect(Collectors.toList());

                    itemDtoResponse.setLastBooking(lastBooking);
                    itemDtoResponse.setNextBooking(nextBooking);
                    itemDtoResponse.setComments(commentDto);

                    return itemDtoResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponse getItemById(int idItem, int idUser) {
        log.debug("Обрабатываем запрос на просмотр предмета с id {}.", idItem);
        ItemDtoResponse item = toItemResponse(exceptionIfNotItem(idItem));

        List<Booking> bookingLastList = bookingRepository.findLastBookingByOwnerId(item.getId(), idUser, BookingStatusEnum.REJECTED, LocalDateTime.now());
        if (bookingLastList.isEmpty()) {
            item.setLastBooking(null);
        } else {
            item.setLastBooking(toBookingDtoResponse(bookingLastList.get(0)));
        }

        List<Booking> bookingNextList = bookingRepository.findNextBookingByOwnerId(item.getId(), idUser, BookingStatusEnum.REJECTED, LocalDateTime.now());
        if (bookingNextList.isEmpty()) {
            item.setNextBooking(null);
        } else {
            item.setNextBooking(toBookingDtoResponse(bookingNextList.get(0)));
        }

        List<CommentDto> listComment = commentRepository.findByItemId(idItem).stream()
                .map(MapperCommentDto::toCommentDto)
                .collect(Collectors.toList());
        if (listComment.isEmpty()) {
            item.setComments(new ArrayList<>());
        } else {
            item.setComments(listComment);
        }
        return item;
    }

    @Override
    public ItemDto postItem(ItemDto itemDto, int idUser) {
        log.debug("Обрабатываем запрос на добавление предмета с названием {}.", itemDto.getName());
        validate(itemDto);
        User user = exceptionIfNotUser(idUser);
        Item item = toItem(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.getReferenceById(itemDto.getRequestId());
            item.setRequest(request);
        } else {
            item.setRequest(null);
        }
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int idItem, int idUser) {
        log.debug("Обрабатываем запрос на редактирование предмета с id {}.", idItem);

        Optional<Item> itemOptional = itemRepository.findById(idItem);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            if (item.getOwner().getId() != idUser) {
                log.warn("Пользователь с id {} пытается изменить не свой предмет с id {}.", idUser, idItem);
                throw new ItemNotFoundException("Вы не являетесь владельцем данного предмета.");
            }
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setIsAvailable(itemDto.getAvailable());
            }
            item.setId(idItem);
            return toItemDto(itemRepository.save(item));
        } else {
            log.warn(NOT_FOUND_ITEM.getValue(), idItem);
            throw new ItemNotFoundException(String.format("Предмета с id %d не существует", idItem));
        }
    }

    @Override
    public void delete(int idItem, int idUser) {
        log.debug("Обрабатываем запрос на удаление предмета с id {}.", idItem);

        Item item = exceptionIfNotItem(idItem);
        if (item.getOwner().getId() != idUser) {
            log.debug("Пользователь с id {} пытается удалить не свой предмет с id {}.", idUser, idItem);
            throw new ItemNotFoundException("Вы не являетесь владельцем данного предмета.");
        }
        itemRepository.deleteById(idItem);
    }

    @Override
    public List<ItemDto> search(String text) {
        log.debug("Обрабатываем запрос по поиску предметов с наличием фрагмента {}.", text);

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(MapperItemDto::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto postComment(CommentDtoJson commentJson, int idUser, int itemId) {
        log.debug("Обрабатываем запрос по добавлению комментария к предмету с id {}.", itemId);

        LocalDateTime localDateTime = LocalDateTime.now();
        User user = exceptionIfNotUser(idUser);
        Item item = exceptionIfNotItem(itemId);

        if (!bookingRepository.existsByBookerIdAndItem_IdAndStatusAndEndBefore(idUser, itemId, BookingStatusEnum.APPROVED, localDateTime)) {
            log.debug("Пользователь с id {} пытается оставить комментарий на предмет с id {} который не брал в аренду", idUser, itemId);
            throw new BookingBadRequest(String.format("Вы не брали данный предмет: %s в аренду или срок вашей аренды еще не закончился", item.getName()));
        }

        if (commentJson.getText().isEmpty()) {
            log.debug("Пользователь с id {} пытается оставить пустой комментарий на предмет с id {}.", idUser, itemId);
            throw new BookingBadRequest("Отзыв не может быть пустым");
        }

        Comment comment = toComment(commentJson);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return toCommentDto(commentRepository.save(comment));
    }

    private Item exceptionIfNotItem(int itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_ITEM.getValue(), itemId);
                    return new ItemNotFoundException(String.format("Предмета с id %d не существует", itemId));
                });
    }

    private User exceptionIfNotUser(int idUser) {
        return userRepository.findById(idUser)
                .orElseThrow(() -> {
                    log.warn(NOT_FOUND_USER.getValue(), idUser);
                    return new UserNotFoundException(String.format("Пользователя с таким id %d не существует.", idUser));
                });
    }
}
