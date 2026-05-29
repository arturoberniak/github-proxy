package com.example.github_proxy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(GithubUserNotFoundException.class)
    ResponseEntity<ErrorResponseDto> handleUserNotFound(GithubUserNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(new ErrorResponseDto(404, ex.getMessage()));
    }
}
