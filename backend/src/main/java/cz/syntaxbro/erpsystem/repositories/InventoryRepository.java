package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<InventoryItem, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE InventoryItem i SET i.quantity = :quantity WHERE i.id = :itemId")
    int updateQuantity(@Param("itemId") Long itemId, @Param("quantity") int quantity);

    Optional<InventoryItem> findByProduct(Product product);
}
