package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("select pc from ProductCategory pc where pc.name = :categoryName")
    Optional<ProductCategory> findByName(@Param("categoryName") String categoryName);
}
