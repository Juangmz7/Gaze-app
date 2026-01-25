package com.juangomez.socialservice.repository;

import com.juangomez.socialservice.config.TestContainersConfig;
import com.juangomez.socialservice.model.entity.Friendship;
import com.juangomez.socialservice.model.enums.FrienshipStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig.class)
class SocialRepositoryTest {

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should find friendship between two users regardless of direction")
    void shouldFindFriendshipBetween() {
        // Given
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();
        Friendship friendship = Friendship.builder()
                .senderId(u1).receiverId(u2)
                .build();
        friendship.updateStatus(FrienshipStatus.ACCEPTED);
        entityManager.persist(friendship);

        // When
        Friendship result = socialRepository.findFriendshipBetween(u2, u1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(FrienshipStatus.ACCEPTED);
    }
}