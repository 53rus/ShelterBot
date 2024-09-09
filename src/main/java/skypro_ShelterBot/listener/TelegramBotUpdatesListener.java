package skypro_ShelterBot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static skypro_ShelterBot.enums.UserType.REGISTERED;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() throws TelegramException {
        telegramBot.setUpdatesListener(this);

    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update.message().chat().id());

            String messageText = update.message().text();
            Long chatId = update.message().chat().id();

            if (messageText.equals("/start")) {

                userService.autoCreateUserGuest(update);

                sendMassage(chatId, "Здравствуйте  , " + update.message().chat().firstName() + " , добро пожаловать в телеграм чат приюта домашних животных\n\n" +
                        "Все пользователи проходят автоматическую регистрацию в базе приюта в роли Гостя");

            } else if (messageText.equals("/help")) {
                sendMassage(chatId, "Для работы с чат ботом воспользуйтесь командами \n\n" +
                        "/start - Приветствие, начало работы\n\n" +
                        "/cat_shelter - Выбрать приют для кошек \n\n" +
                        "/dog_shelter -  Выбрать приют для собак \n\n" +
                        "/cat_shelter_info - Получить информацию по приюту для кошек \n\n" +
                        "/dog_shelter_info - Получить информацию по приюту для собак");

            } else if (messageText.equals("/cat_shelter")) {

            } else if (messageText.equals("/dog_shelter")) {
                sendMassage(chatId, "Приют для собак Мокрый нос");

            } else if (messageText.equals("/cat_shelter_info")) {
                sendMassage(chatId, "Приют для коше Лохматый хвост\n" +
                        "Здесь вы найдете для себя четвероногих друзей\n" +
                        "Адрес: Горбунковский р-н, Кошкина д, Четвероногих друзей ул, дом 1 \n" +
                        "Телефон приюта 8 (818) 333 333 \n" +
                        "Время работы: 9-21 без перерыва на обед, без выходных \n");

            } else if (messageText.equals("/dog_shelter_info")) {
                sendMassage(chatId, "Приют для собак Мокрый нос\n" +
                        "Здесь вы найдете для себя четвероногих друзей\n" +
                        "Адрес: Собачий р-н, Собачья д, Проспект Лающего пса, дом 11 \n" +
                        "Телефон приюта 8 (821) 321 3321 \n" +
                        "Время работы: 9-21 без перерыва на обед, без выходных \n");

            } else sendMassage(chatId, "Для работы с ботом воспользуйтесь меню, либо введите команду /help");

        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private void sendMassage(Long chatId, String answer) {
        SendMessage message = new SendMessage(chatId, answer);
        SendResponse response = telegramBot.execute(message);

    }

}
