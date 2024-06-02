package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestImpTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ItemRequestImp requestServiceImp;

    private final User user = User.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Lopata")
            .description("Lopata description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .items(List.of(item))
            .description("Request description")
            .build();

    @Test
    void whenAddNewRequestIsOk() {
        ItemRequestDto requestDto = ItemRequestMapper.toRequestDto(request);
        ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDtoOut realRequestDtoOut = requestServiceImp.add(user.getId(), requestDto);

        assertEquals(requestDtoOut, realRequestDtoOut);
    }

    @Test
    void whenGetUserRequestsIsOk() {
        List<ItemRequestDtoOut> requestDtoOuts = List.of(ItemRequestMapper.toRequestDtoOut(request));
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findAllByRequesterId(userDto.getId())).thenReturn(List.of(request));

        List<ItemRequestDtoOut> realRequestsDto = requestServiceImp.getUserRequests(userDto.getId());

        assertEquals(requestDtoOuts, realRequestsDto);
    }

    @Test
    void whenGetAllRequestsIsOk() {
        List<ItemRequestDtoOut> requestDtoOuts = List.of(ItemRequestMapper.toRequestDtoOut(request));
        when(itemRequestRepository.findAllByRequester_IdNotOrderByCreatedDesc(anyLong(),
                any(PageRequest.class))).thenReturn(List.of(request));

        List<ItemRequestDtoOut> realRequestDtoOut = requestServiceImp.getAllRequests(user.getId(), 0, 10);

        assertEquals(requestDtoOuts, realRequestDtoOut);
    }

    @Test
    void whenGetRequestByIdIsOk() {
        ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toRequestDtoOut(request);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestDtoOut realRequestDtoOut = requestServiceImp.getRequestById(userDto.getId(), request.getId());

        assertEquals(requestDtoOut, realRequestDtoOut);
    }

    @Test
    void whenGetRequestByIdIsNotValidShouldThrowNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> requestServiceImp.getRequestById(userDto.getId(), request.getId()));

        assertEquals(notFoundException.getMessage(),
                String.format("Request with id= " + request.getId() + " doesn't exist"));
    }
}