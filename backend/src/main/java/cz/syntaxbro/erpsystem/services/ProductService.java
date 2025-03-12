package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.requests.ProductRequest;

public interface ProductService {

    Product createProduct(ProductRequest product);

    Product getProductById(Long id);

    Product getProductByName(String name);

    Product updateProduct(Long id, ProductRequest product);

    void deleteProductById(Long id);

    void deleteProductByName(String name);

    boolean isExistById(Long id);
}
