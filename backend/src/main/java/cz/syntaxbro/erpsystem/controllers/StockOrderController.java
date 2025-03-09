package cz.syntaxbro.erpsystem.controllers;

import cz.syntaxbro.erpsystem.models.StockOrder;
import cz.syntaxbro.erpsystem.services.StockOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-orders")
@RequiredArgsConstructor
public class StockOrderController {

    private final StockOrderService stockOrderService;

    @PostMapping("/create")
    public StockOrder createStockOrder(){
        return stockOrderService.createPendingOrder();
    }
}
