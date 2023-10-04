package ru.practicum.shareit.item.dto.comment;

import ru.practicum.shareit.item.model.Comment;

public class MapperCommentDto {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static Comment toComment(CommentDtoJson commentDto) {
        return new Comment(commentDto.getText());
    }
}
