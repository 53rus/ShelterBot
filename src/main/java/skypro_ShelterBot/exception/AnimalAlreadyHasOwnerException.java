package skypro_ShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AnimalAlreadyHasOwnerException extends RuntimeException
{
    public AnimalAlreadyHasOwnerException() {
        super("Питомец уже нашел своего хозяина");
    }
}
