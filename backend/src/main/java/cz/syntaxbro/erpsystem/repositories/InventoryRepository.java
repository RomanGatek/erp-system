package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository  extends JpaRepository<InventoryItem, Long> {

    /**
     * update quantity in database without getting an item
     *
     * @param itemId
     * @param quantity
     * @return int, number of updated rows
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE InventoryItem i SET i.quantity = :quantity WHERE i.id = :itemId")
    int updateQuantity(@Param("itemId") Long itemId, @Param("quantity") int quantity);
}
