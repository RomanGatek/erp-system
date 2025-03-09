package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
