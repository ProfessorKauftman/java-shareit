package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/*
 * TODO Sprint add-controllers.*/


@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {

    @Autowired
    private ItemService itemService;
    public static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoOut createDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                    @Valid @RequestBody ItemDto itemDto) {
        log.info("POST-request to add an item by a user with id = " + userId + " item " + itemDto.toString());
        return itemService.createItemDto(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut updateDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestBody ItemDto itemDto,
                                    @PathVariable Long itemId) {

        log.info("PATCH-request to update an item with id= " + itemId + " by user with id= " + userId);
        return itemService.updateItemDto(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut getItemDtoById(@RequestHeader(USER_HEADER) Long userId,
                                     @PathVariable Long itemId) {
        log.info("GET-request to get an item with id= " + itemId + " by user with id= " + userId);
        return itemService.findItemDtoById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoOut> getAllDtoItems(@RequestHeader(USER_HEADER) Long userId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET-request to get all items from the user with id= " + userId);
        return itemService.findAllItemsDto(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> searchDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                          @RequestParam String text,
                                          @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET-request to search an item with text = {}", text);
        return itemService.findItemDtoByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut createComment(@RequestHeader(USER_HEADER) Long userId,
                                       @Validated @RequestBody CommentDto commentDto,
                                       @PathVariable Long itemId) {
        log.info("POST-request for comment creation with id= {}", itemId);
        return itemService.createComment(userId, commentDto, itemId);

    }
}
