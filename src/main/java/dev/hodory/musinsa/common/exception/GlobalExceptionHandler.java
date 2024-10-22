package dev.hodory.musinsa.common.exception;

import dev.hodory.musinsa.common.exception.dto.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String MSG_INTERNAL_SERVER_ERROR = "오류가 발생했습니다.";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
        final EntityNotFoundException e) {
        log.debug("handleEntityNotFoundException", e);
        var response = ErrorResponse.of(e.getMessage(), "4040");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityExistsException(
        final EntityExistsException e) {
        log.debug("handleEntityExistsException", e);
        var response = ErrorResponse.of(e.getMessage(), "4001");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException e) {
        log.debug("handleMethodArgumentNotValidException", e);
        var response = ErrorResponse.of(
            e.getBindingResult().getFieldErrors().get(0).getDefaultMessage(), "4003");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        final IllegalArgumentException e) {
        log.debug("handleIllegalArgumentException", e);
        var response = ErrorResponse.of(e.getMessage(), "4002");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException e) {
        log.debug("handleMissingServletRequestParameterException", e);
        var response = ErrorResponse.of(e.getMessage(), "4004");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
        final HttpMessageNotReadableException e) {
        log.debug("handleHttpMessageNotReadableException", e);
        var response = ErrorResponse.of(e.getMessage(), "4005");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.debug("handleHttpRequestMethodNotSupportedException", e);
        var response = ErrorResponse.of("구현되지 않은 엔드포인트입니다.", "4040");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleOtherException(final Exception e) {
        log.debug("handleOtherException", e);
        var response = ErrorResponse.of(e.getMessage(), "5000");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException e) {
        log.debug("handleRuntimeException", e);
        var response = ErrorResponse.of(e.getMessage(), "5000");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
