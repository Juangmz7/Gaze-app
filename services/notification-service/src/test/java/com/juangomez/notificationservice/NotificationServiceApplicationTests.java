package com.juangomez.notificationservice;

import com.juangomez.notificationservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
