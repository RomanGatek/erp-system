package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.repositories.ProductCategoryRepository;
import cz.syntaxbro.erpsystem.requests.ProductCategoryRequest;
import cz.syntaxbro.erpsystem.services.ProductCategoryService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }


    @Override
    public ProductCategory createProductCategory(ProductCategoryRequest productCategory) {

        Optional<ProductCategory> existingProductCategory = productCategoryRepository.findByName(productCategory.getName());
        if(existingProductCategory.isEmpty()) {
            return productCategoryRepository.save(
                    ProductCategory
                            .builder()
                            .name(productCategory.getName())
                            .description(productCategory.getDescription())
                            .products(List.of())
                            .color(productCategory.getColor())
                            .build()
            );
        } else {
            throw new EntityExistsException(String.format("Category with name %s exist", productCategory.getName()));
        }
    }

    @Override
    public ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategory) {
        ProductCategory productCategoryFromDb = getProductCategory(categoryId);
        productCategoryFromDb.setName(productCategory.getName());
        productCategoryFromDb.setDescription(productCategory.getDescription());
        productCategoryFromDb.setColor(productCategory.getColor());
        return productCategoryRepository.save(productCategoryFromDb);
    }

    @Override
    public void deleteProductCategory(long id) {
        ProductCategory productCategoryFromDb = getProductCategory(id);
        productCategoryRepository.deleteById(productCategoryFromDb.getId());

    }

    @Override
    public ProductCategory getProductCategory(long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Override
    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.findAll();
    }
}
