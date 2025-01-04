package org.example.mobile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
public class MobileApplicationTests {

    @Autowired
    private Environment environment;

    @Test
    public void contextLoads() {
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));
    }
}
