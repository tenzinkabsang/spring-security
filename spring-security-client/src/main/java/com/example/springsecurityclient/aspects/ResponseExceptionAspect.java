package com.example.springsecurityclient.aspects;

import com.example.springsecurityclient.models.UserTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseStatus
public class ResponseExceptionAspect extends ResponseEntityExceptionHandler {



    @ExceptionHandler(UserTokenException.class)
    public ResponseEntity<ErrorMessage> tokenNotFound(UserTokenException e,
                                                      WebRequest request) {
        var errorMsg = new ErrorMessage(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(errorMsg.getStatus())
                .body(errorMsg);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> genericException(Exception e, WebRequest request)
    {
        var errorMsg = new ErrorMessage(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(errorMsg.getStatus())
                .body(errorMsg);
    }
}
