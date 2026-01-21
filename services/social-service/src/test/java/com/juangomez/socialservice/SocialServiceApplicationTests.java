package com.juangomez.socialservice;

import com.juangomez.socialservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class SocialServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
