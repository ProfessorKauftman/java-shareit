package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceDtoImpl;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemServiceDtoImpl itemServiceDtoimpl;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                 @Valid
                                 @RequestBody ItemDto itemDto) {
        log.info("POST-request to add an item by a user with id = " + userId + " item " + itemDto.toString());
        return itemServiceDtoimpl.createItemDto(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                 @RequestBody ItemDto itemDto,
                                 @PathVariable Long itemId) {

        log.info("PATCH-request to update an item with id= " + itemId + " by user with id= " + userId);
        return itemServiceDtoimpl.updateItemDto(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@RequestHeader(USER_HEADER) Long userId,
                                  @PathVariable("itemId") Long itemId) {
        log.info("GET-request to get an item with id= " + itemId + " by user with id= " + userId);
        return itemServiceDtoimpl.findItemDtoById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllDtoItems(@RequestHeader(USER_HEADER) Long userId) {
        log.info("GET-request to get all items from the user with id= " + userId);
        return itemServiceDtoimpl.findAllItemsDto(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchDtoItem(@RequestHeader(USER_HEADER) Long userId,
                                       @RequestParam(name = "text") String text) {
        log.info("GET-request to search an item");
        String searchText = text.toLowerCase();
        return itemServiceDtoimpl.findItemDtoByText(userId, searchText);
    }
}
