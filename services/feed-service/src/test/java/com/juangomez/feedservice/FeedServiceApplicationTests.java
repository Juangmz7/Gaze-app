package com.juangomez.feedservice;

import com.juangomez.feedservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class FeedServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
