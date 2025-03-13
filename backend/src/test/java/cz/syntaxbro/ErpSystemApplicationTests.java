package cz.syntaxbro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ErpSystemApplicationTests.class, properties = {"spring.profiles.active=test"})
class ErpSystemApplicationTests {
    @Test
    void contextLoads() {
    }

}
