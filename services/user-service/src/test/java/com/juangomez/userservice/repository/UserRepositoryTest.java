package com.juangomez.userservice.repository;

import com.juangomez.userservice.config.TestContainersConfig;
import com.juangomez.userservice.model.entity.User;
import com.juangomez.userservice.model.enums.UserAccountStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(TestContainersConfig.class)
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;
    @Autowired private TestEntityManager entityManager;

    @Test
    @DisplayName("Should return only active users from list")
    void shouldReturnActiveUsers() {
        // Given
        User activeUser = User.builder().username("active").email("a@a.com").passwordHash("x").build();
        User bannedUser = User.builder().username("banned").email("b@b.com").passwordHash("x").build();
        bannedUser.updateAccountStatus(UserAccountStatus.INACTIVE);

        entityManager.persist(activeUser);
        entityManager.persist(bannedUser);
        entityManager.flush();

        // When
        List<User> result = userRepository.findAllByUsernameInAndStatus(
                Set.of("active", "banned"), UserAccountStatus.ACTIVE
        );

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUsername()).isEqualTo("active");
    }
}