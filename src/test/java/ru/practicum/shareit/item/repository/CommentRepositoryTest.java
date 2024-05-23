package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    public void init() {
        User user = User.builder()
                .name("Professor")
                .email("professor@yandex.ru")
                .build();
        testEntityManager.persistFlushFind(user);

        Item item = Item.builder()
                .name("Item")
                .description("Description")
                .owner(user)
                .available(true)
                .build();
        testEntityManager.persistFlushFind(item);

        Comment comment = Comment.builder()
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .text("Comment")
                .build();
        testEntityManager.persistFlushFind(comment);
    }

    @AfterEach
    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @Test
    void whenFindAllByItemIdWithCommentIsOk() {
        List<Comment> commentList = commentRepository.findAllByItemId(1L);

        assertEquals(commentList.size(), 1);
        assertEquals(commentList.get(0).getText(), "Comment");
    }
}