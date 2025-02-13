package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
