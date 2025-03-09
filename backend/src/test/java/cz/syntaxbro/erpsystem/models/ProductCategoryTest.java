package cz.syntaxbro.erpsystem.models;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductCategoryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void productCategory_CheckIfCategoryNotValueName() {
        //Arrest
        ProductCategory productCategory = ProductCategory.builder()
                .name("")
                .description("test products")
                .build();
        //Act
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        //Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("Name can be null or empty");
    }

    @Test
    void productCategory_CheckIfCategoryIsOk() {
        //Arrest
        ProductCategory productCategory = ProductCategory.builder()
                .name("test")
                .description("test products")
                .build();
        //Act
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(productCategory);
        //Assert
        assertThat(violations).isEmpty();
    }
}
