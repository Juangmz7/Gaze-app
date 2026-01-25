package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.config.TestContainersConfig;
import com.juangomez.feedservice.model.entity.FeedItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfig.class) // Import the config you provided
class FeedRepositoryTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should retrieve feed items from social circle or self-tags")
    void shouldGetFeedCorrectly() {
        // Given
        UUID myId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        String myUsername = "myUser";

        // Post by friend (Should appear)
        FeedItem friendItem = FeedItem.builder()
                .authorId(friendId).postId(UUID.randomUUID()).postBody("Friend content").createdAt(Instant.now()).tags(Set.of())
                .build();

        // Post by stranger tagged with me (Should appear)
        FeedItem taggedItem = FeedItem.builder()
                .authorId(UUID.randomUUID()).postId(UUID.randomUUID()).postBody("Tagged content").createdAt(Instant.now()).tags(Set.of(myUsername))
                .build();

        // Post by stranger not tagged (Should NOT appear)
        FeedItem randomItem = FeedItem.builder()
                .authorId(UUID.randomUUID()).postId(UUID.randomUUID()).postBody("Random content").createdAt(Instant.now()).tags(Set.of())
                .build();

        entityManager.persist(friendItem);
        entityManager.persist(taggedItem);
        entityManager.persist(randomItem);
        entityManager.flush();

        // When
        var result = feedRepository.getFeed(Set.of(myId, friendId), myUsername);

        // Then
        assertThat(result).hasSize(2)
                .extracting(FeedItem::getPostBody)
                .containsExactlyInAnyOrder("Friend content", "Tagged content");
    }
}