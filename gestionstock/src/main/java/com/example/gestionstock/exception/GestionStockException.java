package com.example.gestionstock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GestionStockException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public GestionStockException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public GestionStockException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "ERREUR";
    }
}
