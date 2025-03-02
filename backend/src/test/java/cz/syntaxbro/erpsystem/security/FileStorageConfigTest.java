package cz.syntaxbro.erpsystem.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for the FileStorageConfig class.
 * Ensures that configuration properties are correctly loaded.
 */
@SpringBootTest(properties = {
        "spring.profiles.active=test", // Forces the 'test' profile
        "file.uploadDir=/custom/uploads" // Custom upload directory for testing
})
class FileStorageConfigTest {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    /**
     * Test: Ensures that properties are correctly injected in 'test' profile.
     * Expected Result: The property should be loaded as "/custom/uploads".
     */
    @Test
    void shouldLoadConfiguredUploadDir() {
        assertThat(fileStorageConfig.getUploadDir()).isEqualTo("/custom/uploads");
    }

    /**
     * Test: Ensures the default value is used when no property is set.
     * Expected Result: Default value should be "../frontend/public/uploads".
     */
    @Test
    void shouldUseDefaultUploadDirWhenNotConfigured() {
        // Create a test context **without** setting `file.uploadDir`
        ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                .withPropertyValues("spring.profiles.active=test")
                .withUserConfiguration(FileStorageConfig.class);

        contextRunner.run(context -> {
            FileStorageConfig config = context.getBean(FileStorageConfig.class);
            assertThat(config.getUploadDir()).isEqualTo("../frontend/public/uploads");
        });
    }
}