package one.digitalinnovation.web.herosapi.exceptionhandlers;

import one.digitalinnovation.web.herosapi.exceptions.HeroesException;
import one.digitalinnovation.web.herosapi.model.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class HeroesExceptionHandler {

    @ExceptionHandler(HeroesException.class)
    public Mono<ResponseEntity<ResponseModel>> handleException(HeroesException e) {
        return Mono.fromSupplier(() ->
                ResponseEntity.status(e.httpStatus).body(new ResponseModel(e.getMessage()))
        );
    }
}
