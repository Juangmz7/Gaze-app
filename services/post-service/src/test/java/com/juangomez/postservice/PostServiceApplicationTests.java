package com.juangomez.postservice;

import com.juangomez.postservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class PostServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
