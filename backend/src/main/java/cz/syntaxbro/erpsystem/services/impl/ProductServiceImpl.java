package cz.syntaxbro.erpsystem.services.impl;

import cz.syntaxbro.erpsystem.models.Product;
import cz.syntaxbro.erpsystem.models.ProductCategory;
import cz.syntaxbro.erpsystem.repositories.ProductCategoryRepository;
import cz.syntaxbro.erpsystem.repositories.ProductRepository;
import cz.syntaxbro.erpsystem.requests.ProductRequest;
import cz.syntaxbro.erpsystem.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository =  productCategoryRepository;
    }


    @Override
    public Product createProduct(ProductRequest product) {

        var newProduct = new Product();
        newProduct.setBuyoutPrice(product.getBuyoutPrice());
        newProduct.setPurchasePrice(product.getPurchasePrice());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());

        ProductCategory category = this.productCategoryRepository.findByName(product.getProductCategory())
            .orElseThrow(() -> new RuntimeException("Category not found with name: " + product.getProductCategory()));

        newProduct.setProductCategory(category);
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with name: " + name));
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPurchasePrice(product.getPurchasePrice());
                    existingProduct.setBuyoutPrice(product.getBuyoutPrice());
                    return productRepository.save(existingProduct);
                }).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(
                    productRepository::delete,
                    () -> { throw new EntityNotFoundException("Product not found with id: " + id); }
            );
        }


    @Override
    public void deleteProductByName(String name) {
        productRepository.findByName(name)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> { throw new EntityNotFoundException("Product not found with name: " + name); }
                );
    }

    @Override
    public boolean isExistById(Long id) {
        return productRepository.findById(id).isPresent();
    }
}