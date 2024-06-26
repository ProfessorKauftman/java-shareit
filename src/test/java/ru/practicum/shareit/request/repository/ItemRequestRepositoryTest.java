package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    public void init() {
        User user1 = User.builder()
                .name("Professor")
                .email("professor@yandex.ru")
                .build();
        testEntityManager.persistFlushFind(user1);

        User user2 = User.builder()
                .name("Ne_Professor")
                .email("NeProfessor@yandex.ru")
                .build();
        testEntityManager.persistFlushFind(user2);

        Item item = Item.builder()
                .name("Lopata")
                .description("Lopata description")
                .available(true)
                .owner(user1)
                .build();
        testEntityManager.persistFlushFind(item);

        ItemRequest request1 = ItemRequest.builder()
                .items(List.of(item))
                .description("Request description")
                .created(LocalDateTime.now())
                .requester(user1)
                .build();
        itemRequestRepository.save(request1);

        ItemRequest request2 = ItemRequest.builder()
                .items(List.of(item))
                .description("Request description 2")
                .created(LocalDateTime.now())
                .requester(user2)
                .build();

        itemRequestRepository.save(request2);
    }

    @Test
    void whenFindAllByRequesterInOrderByCreated() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterId(1L);

        assertEquals(requests.size(), 1);
        assertEquals(requests.get(0).getDescription(), "Request description");
    }

}