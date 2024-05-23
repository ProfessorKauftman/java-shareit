package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut add(@RequestHeader(USER_HEADER) Long userId,
                                 @Valid @RequestBody ItemRequestDto requestDto) {
        return itemRequestService.add(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getUserRequests(@RequestHeader(USER_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestHeader(USER_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "0")
                                                  @Min(0) Integer from,
                                                  @RequestParam(defaultValue = "10")
                                                  @Min(1) Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut get(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
