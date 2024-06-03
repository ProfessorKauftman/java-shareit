package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USER_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    private final User user = User.builder()
            .id(1L)
            .name("Professor")
            .email("professor@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .owner(user)
            .build();

    @Test
    @SneakyThrows
    void whenCreateItemItIsValid() {
        Long userId = 0L;
        ItemDto itemDto = ItemDto.builder()
                .description("Item description")
                .name("Item name")
                .available(true)
                .build();

        when(itemService.add(userId, itemDto))
                .thenReturn(ItemMapper.toItemDtoOut(ItemMapper.toItem(itemDto)));

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDto.getName(), resultItemDto.getName());
        assertEquals(itemDto.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void whenUpdateItemIsValidShouldReturnOkStatus() {
        long itemId = 0L;
        long userId = 0L;

        ItemDto itemDto = ItemDto.builder()
                .description("Item description")
                .name("Item name")
                .available(true)
                .build();

        when(itemService.update(userId, itemId, itemDto))
                .thenReturn(ItemMapper.toItemDtoOut(ItemMapper.toItem(itemDto)));

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ItemDto resultItemDto = objectMapper.readValue(result, ItemDto.class);
        assertEquals(itemDto.getName(), resultItemDto.getName());
        assertEquals(itemDto.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    @SneakyThrows
    void whenGetItemShouldReturnStatusOk() {
        long itemId = 0L;
        long userId = 0L;

        ItemDtoOut itemDtoOut = ItemDtoOut.builder()
                .description("")
                .name("")
                .available(true)
                .build();

        when(itemService.findItemById(userId, itemId)).thenReturn(itemDtoOut);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header(USER_HEADER, userId)
                        .content(objectMapper.writeValueAsString(itemDtoOut)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                        + "\"id\":null,"
                        + "\"name\":\"\","
                        + "\"description\":\"\","
                        + "\"available\":true,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "}",
                result);
    }

    @Test
    @SneakyThrows
    void whenGetAllItemsShouldReturnStatusOk() {
        long userId = 0L;
        int from = 0;
        int size = 10;
        List<ItemDtoOut> itemsDto = List.of(ItemDtoOut.builder()
                .name("Some item")
                .description("Some description")
                .available(true)
                .build());

        when(itemService.findAll(userId, from, size)).thenReturn(itemsDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                        + "\"id\":null,"
                        + "\"name\":\"Some item\","
                        + "\"description\":\"Some description\","
                        + "\"available\":true,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "}]",
                result);
    }

    @Test
    @SneakyThrows
    void whenSearchItemsShouldReturnStatusOk() {
        long userId = 0L;
        int from = 0;
        int size = 10;
        String text = "Find";

        List<ItemDtoOut> itemsDto = List.of(ItemDtoOut.builder()
                .name("Some item")
                .description("Some description")
                .available(true)
                .build());

        when(itemService.search(userId, text, from, size)).thenReturn(itemsDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header(USER_HEADER, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[{"
                        + "\"id\":null,"
                        + "\"name\":\"Some item\","
                        + "\"description\":\"Some description\","
                        + "\"available\":true,"
                        + "\"lastBooking\":null,"
                        + "\"comments\":null,"
                        + "\"nextBooking\":null,"
                        + "\"requestId\":null"
                        + "}]",
                result);
    }

    @Test
    @SneakyThrows
    void whenCreateCommentIsValidShouldReturnStatusOk() {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment")
                .build();

        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
                .id(1L)
                .itemId(item.getId())
                .text(commentDto.getText())
                .build();

        when(itemService.createComment(user.getId(), commentDto, item.getId())).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header(USER_HEADER, user.getId())
                        .content(objectMapper.writeValueAsBytes(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("{"
                        + "\"id\":1,"
                        + "\"text\":\"Comment\","
                        + "\"authorName\":null,"
                        + "\"created\":null,"
                        + "\"itemId\":1"
                        + "}",
                result);
    }

}
