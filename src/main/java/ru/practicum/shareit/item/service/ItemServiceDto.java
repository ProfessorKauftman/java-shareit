package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemServiceDto {
    ItemDto createItemDto(Long userId, ItemDto itemDto);

    ItemDto updateItemDto(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findItemDtoById(Long userId, Long itemId);

    List<ItemDto> findAllItemsDto(Long userId);

    List<ItemDto> findItemDtoByText(Long userId, String text);


}
