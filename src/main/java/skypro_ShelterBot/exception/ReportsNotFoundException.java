package skypro_ShelterBot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportsNotFoundException extends RuntimeException{
    public ReportsNotFoundException() {
        super("Отчеты не найдены");
    }
}
