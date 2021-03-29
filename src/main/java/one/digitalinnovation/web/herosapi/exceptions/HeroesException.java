package one.digitalinnovation.web.herosapi.exceptions;

import org.springframework.http.HttpStatus;


public class HeroesException extends RuntimeException {

    final public HttpStatus httpStatus;

    public HeroesException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
