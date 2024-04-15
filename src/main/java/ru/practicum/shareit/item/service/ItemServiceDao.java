package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemServiceDao {
    Item createItem(Item item);
    Item updateItem(Item item);
    Optional<Item> getItemById(Long itemId);
    List<Item> getAllItems(Long userId);
    List<Item> getByText(String text);
}
