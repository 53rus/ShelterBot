package skypro_ShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserDidNotCompleteFullRegistrationException extends RuntimeException{
    public UserDidNotCompleteFullRegistrationException() {
        super("Пользователь не прошел полную регистрацию");
    }
}
