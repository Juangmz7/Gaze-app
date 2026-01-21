package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.config.TestContainersConfig;
import com.juangomez.feedservice.model.entity.FriendshipReplica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfig.class)
class FriendshipRepositoryTest {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should check existence of friendship regardless of ID order if query handles it, or exact match")
    void shouldCheckFriendshipExistence() {
        // Given
        UUID idA = UUID.randomUUID();
        UUID idB = UUID.randomUUID();

        // Simulating the canonical storage (idA < idB) usually enforced by Service
        FriendshipReplica friendship = FriendshipReplica.builder()
                .idA(idA).idB(idB).createdAt(Instant.now())
                .build();

        entityManager.persist(friendship);
        entityManager.flush();

        // When
        // Testing the specific method used in isFriend()
        boolean exists = friendshipRepository
                .existsFriendshipByUser1IdAndUser2Id(friendship.getUser1Id(), friendship.getUser2Id());

        // Then
        assertThat(exists).isTrue();
    }
}