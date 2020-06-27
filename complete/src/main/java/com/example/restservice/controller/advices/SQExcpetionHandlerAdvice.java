package com.example.restservice.controller.advices;

import com.example.restservice.exceptions.SQAccessDeniedException;
import com.example.restservice.exceptions.SQInvalidRequestException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class SQExcpetionHandlerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {SQInvalidRequestException.class})
  protected ResponseEntity<Object> handlerInvalidReq(
      SQInvalidRequestException ex, WebRequest request) {
    var response =
        new DefaultErrorAttributes()
            .getErrorAttributes(
                request, false /* includeStackTrace */);
    var httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
    response.put("reasonCode", ex.getReasonCode());
    response.put("status", httpStatus.value());
    response.put("error", httpStatus.getReasonPhrase());
    response.put("message", ex.getMessage());
    return handleExceptionInternal(ex, response, new HttpHeaders(), httpStatus, request);
  }

  @ExceptionHandler(value = {SQAccessDeniedException.class})
  protected ResponseEntity<Object> handlerIAccessDenied(
      SQAccessDeniedException ex, WebRequest request) {
    var response =
        new DefaultErrorAttributes()
            .getErrorAttributes(
                request, false /* includeStackTrace */);
    var httpStatus = HttpStatus.UNAUTHORIZED;
    response.put("status", httpStatus.value());
    response.put("error", httpStatus.getReasonPhrase());
    response.put("message", ex.getMessage());
    return handleExceptionInternal(ex, response, new HttpHeaders(), httpStatus, request);
  }
}
