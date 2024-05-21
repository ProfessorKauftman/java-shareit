package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImp itemServiceImp;

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
            .status(Status.APPROVED)
            .start(LocalDateTime.now().minusDays(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking lastBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .id(3L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().minusDays(10L))
            .end(LocalDateTime.now().minusDays(9L))
            .build();

    private final Booking nextBooking = Booking.builder()
            .id(4L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .id(5L)
            .item(item)
            .booker(user)
            .status(Status.APPROVED)
            .start(LocalDateTime.now().plusDays(10L))
            .end(LocalDateTime.now().plusDays(20L))
            .build();


    @Test
    void addNewItemWhenInvoke() {
        Item saveItem = Item.builder()
                .name("Test name")
                .description("Test description")
                .available(true)
                .build();

        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.save(saveItem)).thenReturn(saveItem);

        ItemDtoOut realItemDtoOut = itemServiceImp.createItemDto(userDto.getId(), ItemMapper.toItemDto(saveItem));

        assertEquals(realItemDtoOut.getName(), "Test name");
        assertEquals(realItemDtoOut.getDescription(), "Test description");
    }

    @Test
    void whenGetItemByIdIsCorrect() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ItemDtoOut actualItemDto = itemServiceImp.findItemDtoById(user.getId(), item.getId());

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

        ItemDtoOut saveItem = itemServiceImp.updateItemDto(user.getId(), itemDto.getId(),
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
                () -> itemServiceImp.updateItemDto(user.getId(), itemDto.getId(), ItemMapper.toItemDto(updateItem)));

        assertEquals(notFoundException.getMessage(), "User with id= " + user.getId() +
                " isn't the owner of the item with id= " + item.getId());
    }

    @Test
    void whenUpdateItemWithNotValidId() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImp.updateItemDto(user.getId(), itemDto.getId(), ItemMapper.toItemDto(item)));
        assertEquals(notFoundException.getMessage(), "Item with id= " + item.getId() + " doesn't exist");
    }

    @Test
    void whenGetAllCommentsIsCorrect() {
        List<CommentDtoOut> commentDtoOutList = List.of(CommentMapper.toCommentDtoOut(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentDtoOut> realComments = itemServiceImp.getAllItemComments(item.getId());

        assertEquals(commentDtoOutList.size(), 1);
        assertEquals(commentDtoOutList, realComments);
    }

    @Test
    void whenSearchItemIsCorrect() {
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDtoOut> realItems = itemServiceImp.findAllItemsDto(1L, 0, 10);

        assertEquals(1, realItems.size());
        assertEquals(1, realItems.get(0).getId());
        assertEquals("Lopata", realItems.get(0).getName());
    }

    @Test
    void whenCreateCommentIsCorrect() {
        CommentDtoOut commentDtoOut = CommentMapper.toCommentDtoOut(comment);
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoOut realComment = itemServiceImp.createComment(user.getId(), CommentMapper.toCommentDto(comment),
                item.getId());

        assertEquals(commentDtoOut, realComment);
    }

    @Test
    void whenCreateCommentAndItemIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemServiceImp.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(notFoundException.getMessage(), "User with id= " + user.getId() +
                " doesn't have item with id= " + item.getId());
    }

    @Test
    void whenCreateCommentAndUserHasNotBookingsShouldThrowValidationException() {
        when(userService.findById(user.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemServiceImp.createComment(user.getId(), CommentMapper.toCommentDto(comment), item.getId()));

        assertEquals(validationException.getMessage(), "User with id= " + user.getId() +
                " there must be at least one booking of an item with an id= " + item.getId());

    }
}