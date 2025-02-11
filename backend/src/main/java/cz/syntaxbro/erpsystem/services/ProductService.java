package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Product;

public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    Product getProductByName(String name);

    Product updateProduct(Long id, Product product);

    void deleteProductById(Long id);

    void deleteProductByName(String name);
}
