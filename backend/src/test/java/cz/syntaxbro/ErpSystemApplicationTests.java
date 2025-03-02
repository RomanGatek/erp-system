package cz.syntaxbro;

import cz.syntaxbro.erpsystem.ErpSystemApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ErpSystemApplication.class, properties = {"spring.profiles.active=test"})
class ErpSystemApplicationTests {

    @Test
    void contextLoads() {
    }

}
