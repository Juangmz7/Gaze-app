package com.juangomez.feedservice.repository;

import com.juangomez.feedservice.config.TestContainersConfig;
import com.juangomez.feedservice.model.entity.UserReplica;
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
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainersConfig.class)
class UserReplicaRepositoryTest {

    @Autowired
    private UserReplicaRepository userReplicaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should find user replica by username")
    void shouldFindUserByUsername() {
        // Given
        UserReplica user = UserReplica.builder()
                .id(UUID.randomUUID())
                .username("uniqueUser")
                .email("test@test.com")
                .build();

        entityManager.persist(user);
        entityManager.flush();

        // When
        var result = userReplicaRepository.findByUsername("uniqueUser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
    }
}