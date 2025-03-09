package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.Order;
import cz.syntaxbro.erpsystem.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select distinct o from Order o join o.orderItems oi where oi.inventoryItem.product = :product")
    List<Order> findByProduct(@Param("product") Product product);

    @Modifying
    @Query("delete from Order o where exists (select 1 from OrderItem oi where oi.inventoryItem.product = :product)")
    void deleteByProduct(@Param("product") Product product);

    List<Order> findByStatus(Order.Status status);
}
