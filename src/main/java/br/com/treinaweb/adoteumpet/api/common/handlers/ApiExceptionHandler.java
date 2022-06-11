package br.com.treinaweb.adoteumpet.api.common.handlers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;

import br.com.treinaweb.adoteumpet.api.common.dtos.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final SnakeCaseStrategy snakeCaseStrategy = new SnakeCaseStrategy();

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers, 
        HttpStatus status, 
        WebRequest request
        ) {
            var body = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .cause(ex.getClass().getSimpleName())
                .message("Validate Errors")
                .timestamp(LocalDateTime.now())
                .errors(convertFieldErrors(ex.getBindingResult().getFieldErrors()))
                .build();

            ex.getBindingResult().getFieldError();
            return new ResponseEntity<>(body, status);
    }

    private Map<String, List<String>> convertFieldErrors(List<FieldError> FieldError) {
        var errors = new HashMap<String, List<String>>();
        FieldError.stream().forEach(fieldError -> {
            var field = snakeCaseStrategy.translate(fieldError.getField());
            if (errors.containsKey(field)) {
                errors.get(field).add(fieldError.getDefaultMessage());
            } else {
                errors.put(
                    field, 
                    Stream.of(fieldError.getDefaultMessage())
                        .collect(Collectors.toList())
                );
            }
        });
        return errors;
    } 

}
