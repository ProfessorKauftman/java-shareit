package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getText());
    }

    public Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getText(),
                item,
                user
        );
    }

    public CommentDtoOut toCommentDtoOut(Comment comment) {
        return new CommentDtoOut(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getItem().getId());
    }
}
