package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<InventoryItem, Long> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE InventoryItem i SET i.stockedAmount = :stockedAmount WHERE i.id = :id")
    int updateQuantity(@Param("id") Long id, @Param("stockedAmount") int stockedAmount);

    Optional<InventoryItem> findByProduct(Product product);

    @Query("SELECT i FROM InventoryItem i WHERE i.createdAt BETWEEN :start AND :end")
    List<InventoryItem> findAllByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
