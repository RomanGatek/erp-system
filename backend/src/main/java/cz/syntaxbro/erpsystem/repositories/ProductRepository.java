package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
}
