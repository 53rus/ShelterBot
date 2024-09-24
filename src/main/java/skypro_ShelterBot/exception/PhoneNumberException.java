package skypro_ShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.BAD_REQUEST)
public class PhoneNumberException extends RuntimeException{
    public PhoneNumberException(String message) {
        super(message);
    }
}
