package cz.syntaxbro.erpsystem.services;


import cz.syntaxbro.erpsystem.models.StockOrder;
import cz.syntaxbro.erpsystem.models.StockOrderStatus;
import cz.syntaxbro.erpsystem.repositories.StockOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockOrderService {

    private final StockOrderRepository stockOrderRepository;

    public StockOrder createPendingOrder(){
        StockOrder stockOrder = StockOrder.builder()
                .status(StockOrderStatus.PENDING)
                .build();
        return stockOrderRepository.save(stockOrder);
    }
}
