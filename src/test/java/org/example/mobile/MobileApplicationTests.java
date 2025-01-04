package org.example.mobile;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class MobileApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Active Profiles:");
    }

}
