package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrder(Order order);
    Optional<OrderItem> findByInventoryItem(InventoryItem item);

}
