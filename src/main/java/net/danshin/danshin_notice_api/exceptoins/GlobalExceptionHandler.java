package net.danshin.danshin_notice_api.exceptoins;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<@NonNull String> handleDataNotFoundException(DataNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Data not found: " + ex.getMessage());
    }

    @ExceptionHandler(DataNotValidException.class)
    public ResponseEntity<@NonNull String> handleDataNotValidException(DataNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Data not valid: " + ex.getMessage());
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<@NonNull String> handleNoContentException(NoContentException ex) {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("No content: " + ex.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<@NonNull String> handleServerException(ServerException ex) {
        return ResponseEntity
                .status(HttpStatus.valueOf(500))
                .body(ex.getMessage());
    }
}

