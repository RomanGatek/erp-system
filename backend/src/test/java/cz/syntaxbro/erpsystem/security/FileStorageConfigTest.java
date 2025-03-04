package cz.syntaxbro.erpsystem.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test"})
class FileStorageConfigTest {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    /**
     * Tests loading value from `application-test.properties`.
     * Ensures that the application correctly reads configuration from an external file.
     */
    @Test
    void testFileStorageConfigFromApplicationTestProperties() {
        assertThat(fileStorageConfig.getUploadDir()).isNotNull();
        assertThat(fileStorageConfig.getUploadDir()).isEqualTo("../frontend/public/uploads");
    }
}