package cz.syntaxbro.erpsystem.models.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.syntaxbro.erpsystem.deserializers.OrderDeserializers;
import cz.syntaxbro.erpsystem.models.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;

    private Long productId;

    @JsonDeserialize(using = OrderDeserializers.StringDeserializer.class)
    private String productName;

    @JsonDeserialize(using = OrderDeserializers.IntegerDeserializer.class)
    private int amount;

    @JsonDeserialize(using = OrderDeserializers.DoubleDeserializer.class)
    private Double cost;

    @JsonDeserialize(using = OrderDeserializers.LocalDateTimeDeserializer.class)
    private LocalDateTime orderTime;

    @JsonDeserialize(using = OrderDeserializers.StatusDeserializer.class)
    private String status;

}