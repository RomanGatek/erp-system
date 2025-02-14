package cz.syntaxbro.erpsystem.services;

import cz.syntaxbro.erpsystem.models.Product;

public interface ProductService {

    Product getProductById(Long id);
}
