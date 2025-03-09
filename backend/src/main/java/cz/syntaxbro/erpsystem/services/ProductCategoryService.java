package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.ProductCategory;

import java.util.List;

public interface ProductCategoryService {

    void createProductCategory(ProductCategory productCategory);

    void updateProductCategory(Long categoryId, ProductCategory productCategory);

    void deleteProductCategory(long id);

    ProductCategory getProductCategory(long id);

    List<ProductCategory> getProductCategories();
}
