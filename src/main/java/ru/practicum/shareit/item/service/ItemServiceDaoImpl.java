package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemServiceDaoImpl implements ItemServiceDao {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generatorId = 0L;

    private long getId() {
        return ++generatorId;
    }


    @Override
    public Item createItem(Item item) {
        Long itemId = getId();
        item.setId(itemId);
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> userItems = items.values().stream()
                .filter(item -> userId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
        return userItems;
    }

    @Override
    public List<Item> getByText(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
