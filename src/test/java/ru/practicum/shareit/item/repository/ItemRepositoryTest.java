package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager testEntityManager;


    @BeforeEach
    public void addItems() {
        User user = User.builder()
                .name("Professor")
                .email("professor@yandex.ru")
                .build();
        testEntityManager.persistFlushFind(user);

        Item item = Item.builder()
                .name("item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        itemRepository.save(item);
    }

    @AfterEach
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void whenFindAllItemsByOwnerIdInAscOrderIsOk() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(1L,
                PageRequest.of(0, 1)).getContent();

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "item");
    }

    @Test
    void whenFindItemByDescriptionIsOk() {
        String searchQuery = "desc";

        List<Item> foundItems = itemRepository.search(searchQuery,
                PageRequest.of(0, 1));
        assertFalse(foundItems.isEmpty());
        assertTrue(foundItems.stream().anyMatch(item -> item.getDescription().toLowerCase().contains(searchQuery)));
    }

}