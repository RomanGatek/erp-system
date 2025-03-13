package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.InventoryItem;
import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrder(Order order);
    Optional<OrderItem> findByInventoryItem(InventoryItem item);
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id IN :orderIds")

    List<OrderItem> findByOrderIdIn(@Param("orderIds") Set<Long> orderIds);
}
