package cz.syntaxbro.erpsystem.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import cz.syntaxbro.erpsystem.models.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class OrderDeserializers {
    public static class IntegerDeserializer extends JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String value = p.getValueAsString();
            try {
                if (value.contains(".")) {
                    throw new IllegalArgumentException("Value must be a whole number without decimals");
                }
                int number = Integer.parseInt(value);
                if (number <= 0) {
                    throw new IllegalArgumentException("Value must be positive");
                }
                return number;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value must be a valid integer");
            }
        }
    }

    public static class DoubleDeserializer extends JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String value = p.getValueAsString();
            try {
                double number = Double.parseDouble(value);
                if (number <= 0) {
                    throw new IllegalArgumentException("Value must be positive");
                }

                BigDecimal bd = new BigDecimal(number);
                bd = bd.setScale(2, RoundingMode.HALF_UP);

                if (bd.doubleValue() != number) {
                    throw new IllegalArgumentException("Value must have exactly 2 decimal places");
                }

                return bd.doubleValue();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Value must be a valid number");
            }
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String value = p.getValueAsString();
            try {
                LocalDateTime dateTime = LocalDateTime.parse(value);

                String pattern = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d";
                if (!value.matches(pattern)) {
                    throw new IllegalArgumentException("Invalid date format. Use: yyyy-MM-ddTHH:mm:ss");
                }

                if (dateTime.isAfter(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Date cannot be in the future");
                }

                return dateTime;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss");
            }
        }
    }

    public static class StatusDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String value = p.getValueAsString();
            try {
                return String.valueOf(Order.Status.fromString(value));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status. Valid values are: "
                        + java.util.Arrays.toString(Order.Status.values()));
            }
        }
    }

    public static class StringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            // Kontrola, či je hodnota typu String
            if (!p.getCurrentToken().isScalarValue() || p.getCurrentToken().isNumeric()) {
                throw new IllegalArgumentException("Value must be a string");
            }

            String value = p.getValueAsString();

            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("String value cannot be null or empty");
            }

            // Odstráni medzery na začiatku a konci
            value = value.trim();

            return value;
        }
    }
}
