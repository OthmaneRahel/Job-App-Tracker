package jobtrackerapp.servicenotifications.exceptions;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandller {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleException(Exception ex){
        return ResponseEntity.badRequest().body("Voila erreur "+ex.getMessage());
    }
}
