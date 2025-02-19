package cz.syntaxbro.erpsystem.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("details", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", ex.getMessage());

        //info o requeste
        response.put("method", httpRequest.getMethod());
        response.put("requestURL", httpRequest.getRequestURL().toString());

        //info o exception
        response.put("exceptionType", ex.getClass().getSimpleName());

        // Basic info z WebRequest
        response.put("parameters", request.getParameterMap());
        response.put("contextPath", request.getContextPath());

        // Ak je to ServletWebRequest este viac info
        if (request instanceof ServletWebRequest servletRequest) {
            response.put("userAgent", servletRequest.getHeader("User-Agent"));
            response.put("contentType", servletRequest.getHeader("Content-Type"));
            System.out.println("\n\n");
        }

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralExceptions(Exception ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerExceptions(NullPointerException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", "A required field is missing or null: " + ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormatException(
            NumberFormatException e, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        String fieldName = extractFieldName(request);
        String message = getAppropriateMessage(fieldName);

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Number Format");
        response.put("message", message);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private String extractFieldName(WebRequest request) {
        String path = request.getDescription(false);
        return path;
    }

    private String getAppropriateMessage(String fieldName) {
        switch (fieldName.toLowerCase()) {
            case "amount":
                return "Amount must be an integer";
            case "cost":
                return "Cost must be a decimal number with 2 decimal places";
            case "id":
                return "ID must be an integer";
            default:
                return "Invalid number format";
        }
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(
            TypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();
        String expectedType = ex.getRequiredType().getSimpleName();
        String fieldName = ex.getPropertyName();

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Type Mismatch");
        response.put("message", String.format("Field '%s' must be of type %s", fieldName, expectedType));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        Map<String, Object> response = new HashMap<>();
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        Object value = ex.getValue();

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Type Mismatch");
        response.put("message", String.format("Parameter '%s' should be of type %s. Value '%s' is invalid",
                name, type, value));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();
        String message = "Invalid request body format";

        if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException jme = (JsonMappingException) ex.getCause();
            String fieldName = jme.getPath().isEmpty() ? "" : jme.getPath().get(0).getFieldName();

            if (fieldName.equals("amount")) {
                message = "Amount must be an integer number";
            } else if (fieldName.equals("cost")) {
                message = "Cost must be a decimal number with 2 decimal places";
            } else {
                message = String.format("Invalid value for field '%s'", fieldName);
            }
        }

        response.put("timestamp", LocalDateTime.now().format(formater));
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Invalid Format");
        response.put("message", message);
        response.put("details", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}