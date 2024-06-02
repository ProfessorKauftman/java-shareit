package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    ItemDtoOut createItemDto(Long userId, ItemDto itemDto);

    ItemDtoOut updateItemDto(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoOut findItemDtoById(Long userId, Long itemId);

    List<ItemDtoOut> findAllItemsDto(Long userId, Integer from, Integer size);

    List<ItemDtoOut> findItemDtoByText(Long userId, String text, Integer from, Integer size);

    CommentDtoOut createComment(Long userId, CommentDto commentDto, Long itemId);
}
