package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    private final User user = User.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("Ne_professor")
            .email("NePropfessor@yandex.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("DtoProfessor")
            .email("DtoProfessor@Yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Lopata")
            .description("Description for Lopata")
            .available(true)
            .owner(user)
            .build();

    private final ItemDtoOut itemDto = ItemDtoOut.builder()
            .id(1L)
            .name("Lopata")
            .description("Description for Lopata")
            .available(true)
            .comments(Collections.emptyList())
            .build();

    private final Comment comment = Comment.builder()
            .id(1L)
            .text("Comment")
            .created(LocalDateTime.now())
            .author(user)
            .item(item)
            .build();

    private final Booking booking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    @Test
    void addNewItemWhenInvoked() {
        Item itemSaveTest = Item.builder()
                .name("test item name")
                .description("test description")
                .available(true)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.save(itemSaveTest)).thenReturn(itemSaveTest);

        ItemDtoOut actualItemDto = itemServiceImpl.add(userDto.getId(), ItemMapper.toItemDto(itemSaveTest));

        assertEquals(actualItemDto.getName(), "test item name");
        assertEquals(actualItemDto.getDescription(), "test description");
    }

    @Test
    void whenGetItemByIdIsCorrect() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemServiceImpl.findItemById(user.getId(), item.getId());

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void whenUpdateItemIsCorrect() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now(), null);
        Item updateItem = Item.builder()
                .id(1L)
                .name("Updated name")
                .description("Updated description")
                .available(false)
                .owner(user)
                .request(itemRequest)
                .build();

        when(userService.findById(user.getId())).thenReturn(UserMapper.toUserDto(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updateItem));

        ItemDtoOut saveItem = itemServiceImpl.update(user.getId(), itemDto.getId(),
                ItemMapper.toItemDto(updateItem));

        assertEquals("Updated name", saveItem.getName());
        assertEquals("Updated description", saveItem.getDescription());
    }

    @Test
    void whenUpdateItemAndUserIsNotItemOwnerShouldThrowException() {
        Item updateItem = Item.builder()
                .id(1L)
                .name("Updated name")
                .description("Updated description")
                .available(false)
                .owner(user2)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(updateItem));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updateItem)));

        assertEquals(notFoundException.getMessage(), "User with id= " + user.getId() +
                " is not the owner of the item with id= " + item.getId());
    }

    @Test
    void whenUpdateItemWithNotValidId() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.update(user.getId(), itemDto.getId(), ItemMapper.toItemDto(item)));
        assertEquals(notFoundException.getMessage(), "Item with id= " + item.getId() + " doesn't exist");
    }

    @Test
    void whenGetAllCommentsIsCorrect() {
        List<CommentDtoOut> commentDtoOutList = List.of(CommentMapper.toCommentDtoOut(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentDtoOut> realComments = itemServiceImpl.getAllItemComments(item.getId());

        assertEquals(commentDtoOutList.size(), 1);
        assertEquals(commentDtoOutList, realComments);
    }

    @Test
    void whenCreateCommentIsCorrect() {
        CommentDtoOut commentDtoOut = CommentMapper.toCommentDtoOut(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoOut realComment = itemServiceImpl.createComment(user.getId(), CommentMapper.toCommentDto(comment),
                item.getId());

        assertEquals(commentDtoOut, realComment);
    }

    @Test
    void whenCreateCommentAndItemIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImpl.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(notFoundException.getMessage(), "User with id= " + user.getId() +
                " doesn't have the item with id= " + item.getId());
    }

    @Test
    void whenCreateCommentAndUserHasNotBookingsShouldThrowValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemServiceImpl.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(validationException.getMessage(), "User with id= " + user.getId() +
                " must be at least one booking of an item with an id= " + item.getId());

    }

    @Test
    void searchShouldReturnEmptyListWhenTextIsBlank() {

        Long userId = 1L;
        String searchText = "  ";
        Integer from = 0;
        Integer size = 10;

        when(userService.findById(userId)).thenReturn(userDto);

        List<ItemDtoOut> result = itemServiceImpl.search(userId, searchText, from, size);


        assertTrue(result.isEmpty());

        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    void searchShouldReturnItemsWhenTextIsNotBlank() {
        Long userId = 1L;
        String searchText = "Lopata";
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from, size);

        List<Item> searchResultList = List.of(item);

        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.search(searchText, pageable)).thenReturn(searchResultList);

        List<ItemDtoOut> result = itemServiceImpl.search(userId, searchText, from, size);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        ItemDtoOut expectedDto = ItemMapper.toItemDtoOut(item);
        ItemDtoOut actualDto = result.get(0);

        assertEquals(expectedDto.getId(), actualDto.getId());
        assertEquals(expectedDto.getName(), actualDto.getName());
        assertEquals(expectedDto.getDescription(), actualDto.getDescription());

        verify(userService).findById(userId);
        verify(itemRepository).search(searchText, pageable);
    }

    @Test
    void findAllShouldReturnItemDtosForGivenUser() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from / size, size);

        List<Item> foundItems = List.of(mock(Item.class));

        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(foundItems);
        List<ItemDtoOut> result = itemServiceImpl.findAll(userId, from, size);

        assertNotNull(result);
        assertEquals(foundItems.size(), result.size());

        verify(itemRepository).findAllByOwnerId(userId, pageable);

    }

}