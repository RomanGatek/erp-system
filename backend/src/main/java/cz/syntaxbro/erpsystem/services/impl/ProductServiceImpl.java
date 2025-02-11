package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.services.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    @Override
    public Product createProduct(Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new IllegalArgumentException("product name is required");
        }
        if (product.getCost() <= 0) {
            throw new IllegalArgumentException("product cost must be greater than zero");
        }
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with name " + name));
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getCost() > 0) {
            existingProduct.setCost(product.getCost());
        }
        if (product.getQuantity() >= 0) {
            existingProduct.setQuantity(product.getQuantity());
        }
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public void deleteProductByName(String name) {
        Product product = getProductByName(name);
        productRepository.delete(product);
    }
}
