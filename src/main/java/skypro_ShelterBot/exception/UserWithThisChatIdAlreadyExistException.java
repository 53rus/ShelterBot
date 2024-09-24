package skypro_ShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserWithThisChatIdAlreadyExistException extends RuntimeException {
    public UserWithThisChatIdAlreadyExistException() {
        super("Пользователь с текущим chatId уже существует, прейдите в редактирование существующего контакта");
    }
}
