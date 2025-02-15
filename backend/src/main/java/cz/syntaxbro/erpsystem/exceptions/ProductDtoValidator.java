package cz.syntaxbro.erpsystem.exceptions;

import cz.syntaxbro.erpsystem.models.dtos.ProductDto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ProductDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> productClass){
        return ProductDto.class.equals(productClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductDto productDto = (ProductDto) target;
        if (productDto.getName() != null && !(productDto.getName() instanceof String)) {
            errors.rejectValue("name","type.mismatch","Name must be a string");
        }

        if (productDto.getCost() != null && !(productDto.getCost() instanceof Double)) {
            errors.rejectValue("cost","type.mismatch","Cost must be a double");
        }

        if (productDto.getQuantity() != null && !(productDto.getQuantity() instanceof Integer)) {
            errors.rejectValue("quantity","type.mismatch","Quantity must be an integer");
        }
    }
}
