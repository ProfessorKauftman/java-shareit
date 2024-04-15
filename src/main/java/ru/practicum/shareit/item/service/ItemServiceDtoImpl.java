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
import java.util.Objects;
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
        item.setOwnerId(UserMapper.DtoToUser(userDto).getId());
        return ItemMapper.ItemToDto(serviceDao.createItem(item));
    }

    @Override
    public ItemDto updateItemDto(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> itemOptional = serviceDao.getItemById(itemId);
        if (itemOptional.isPresent()) {
            if (!itemOptional.get().getOwnerId().equals(userId)) {
                throw new NotFoundException(String.format("User with id %s " +
                        "isn't the owner of the item with id %s", userId, itemId));
            }
            Item itemFromStorage = itemOptional.get();
            Item item = ItemMapper.dtoToItem(itemDto);
            if (Objects.isNull(item.getAvailable())) {
                item.setAvailable(itemFromStorage.getAvailable());
            }
            if (Objects.isNull(item.getDescription())) {
                item.setDescription(itemFromStorage.getDescription());
            }
            if (Objects.isNull(item.getName())) {
                item.setName(itemFromStorage.getName());
            }
            item.setId(itemFromStorage.getId());
            item.setRequestId(itemFromStorage.getRequestId());
            item.setOwnerId(itemFromStorage.getOwnerId());

            return ItemMapper.ItemToDto(serviceDao.updateItem(item));
        }
        return itemDto;
    }

    @Override
    public ItemDto findItemDtoById(Long userId, Long itemId) {
        userServiceDaoImpl.findUserById(userId);
        Optional<Item> getItem = serviceDao.getItemById(itemId);
        if (getItem.isEmpty()) {
            throw new NotFoundException(String.format("User with id %s"
                    + " doesn't have item with id %s", userId, itemId));
        }
        return ItemMapper.ItemToDto(getItem.get());
    }

    @Override
    public List<ItemDto> findAllItemsDto(Long userId) {
        userServiceDaoImpl.findUserById(userId);
        List<Item> itemList = serviceDao.getAllItems(userId);
        return itemList.stream()
                .map(ItemMapper::ItemToDto)
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
                .map(ItemMapper::ItemToDto)
                .collect(Collectors.toList());
    }
}
