package cz.syntaxbro.erpsystem.repositories;

import cz.syntaxbro.erpsystem.models.StockOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockOrderRepository extends JpaRepository<StockOrder, Long> {
}
