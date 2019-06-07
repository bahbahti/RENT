package com.netcracker.exception;

import com.netcracker.pojoServices.StatusDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<StatusDTO> handleSQLException(SQLException e) {
        StatusDTO statusDTO = StatusDTO.create("ERROR");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(statusDTO);
    }
    
}
