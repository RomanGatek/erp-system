package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.requests.ProductCategoryRequest;

import java.util.List;

public interface ProductCategoryService {

    ProductCategory createProductCategory(ProductCategoryRequest productCategory);

    ProductCategory updateProductCategory(Long categoryId, ProductCategoryRequest productCategory);

    void deleteProductCategory(long id);

    ProductCategory getProductCategory(long id);

    List<ProductCategory> getProductCategories();
}
