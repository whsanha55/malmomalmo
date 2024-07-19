package com.demo.malmo.global.exception;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<String> handleValidationException(BindException exception) {
        var message = Optional.ofNullable(exception.getFieldError())
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse("");
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<Void> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<String> handleBaseException(BaseException exception) {
        log.info("handleBaseException : {}", exception);
        return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<String> handlerException(Exception exception) {
        log.error("", exception);
        var message = exception.getClass().getSimpleName() + ":::" + exception.getLocalizedMessage();
        return ResponseEntity.internalServerError().body(message);
    }

}
