package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserServiceDaoImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceDtoImpl implements ItemServiceDto {
    private final ItemServiceDao serviceDao;
    private final UserServiceDaoImpl userServiceDaoImpl;


    @Override
    public ItemDto createItemDto(Long userId, ItemDto itemDto) {
        UserDto userDto = UserMapper.userToDto(userServiceDaoImpl.findUserById(userId));
        Item item = ItemMapper.dtoToItem(itemDto);
        item.setOwnerId(UserMapper.dtoToUser(userDto).getId());
        return ItemMapper.itemToDto(serviceDao.createItem(item));
    }

    @Override
    public ItemDto updateItemDto(Long userId, Long itemId, ItemDto itemDto) {
        return serviceDao.getItemById(itemId)
                .map(itemFromStorage -> {
                    if (!itemFromStorage.getOwnerId().equals(userId)) {
                        throw new NotFoundException(
                                String.format("User with id %s isn't the owner of the item with id %s", userId, itemId));
                    }
                    Item itemToUpdate = ItemMapper.dtoToItem(itemDto);
                    itemToUpdate.setId(itemId);
                    itemToUpdate.setOwnerId(userId);

                    itemToUpdate.setAvailable(Optional
                            .ofNullable(itemToUpdate.getAvailable())
                            .orElse(itemFromStorage.getAvailable()));
                    itemToUpdate.setDescription(Optional
                            .ofNullable(itemToUpdate.getDescription())
                            .orElse(itemFromStorage.getDescription()));
                    itemToUpdate.setName(Optional
                            .ofNullable(itemToUpdate.getName())
                            .orElse(itemFromStorage.getName()));
                    itemToUpdate.setRequestId(itemFromStorage.getRequestId());
                    return ItemMapper.itemToDto(serviceDao.updateItem(itemToUpdate));
                }).orElse(itemDto);
    }

    @Override
    public ItemDto findItemDtoById(Long userId, Long itemId) {
        userServiceDaoImpl.findUserById(userId);
        return serviceDao.getItemById(itemId)
                .map(ItemMapper::itemToDto)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %s"
                        + " doesn't have item with id %s", userId, itemId)));
    }

    @Override
    public List<ItemDto> findAllItemsDto(Long userId) {
        userServiceDaoImpl.findUserById(userId);
        List<Item> itemList = serviceDao.getAllItems(userId);
        return itemList.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemDtoByText(Long userId, String text) {
        userServiceDaoImpl.findUserById(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> itemList = serviceDao.getByText(text);
        return itemList.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }
}
