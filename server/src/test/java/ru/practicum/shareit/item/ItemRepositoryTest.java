package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("professor")
            .email("professor@yandex.ru")
            .build();

    private final Item item = Item.builder()
            .name("Lopata")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    @BeforeEach
    public void addItems() {
        testEntityManager.persist(user);
        testEntityManager.flush();
        itemRepository.save(item);
    }

    @AfterEach
    public void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1)).getContent();

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "Lopata");
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