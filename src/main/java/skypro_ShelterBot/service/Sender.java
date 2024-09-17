package skypro_ShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;

@Component
public class Sender {
    private final TelegramBot telegramBot;

    public Sender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendMassage(Long chatId, String answer) {
        SendMessage message = new SendMessage(chatId, answer);
        SendResponse response = telegramBot.execute(message);
    }
}
