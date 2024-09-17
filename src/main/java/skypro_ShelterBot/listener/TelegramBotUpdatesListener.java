package skypro_ShelterBot.listener;

import ch.qos.logback.core.util.FixedDelay;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.service.AnimalService;
import skypro_ShelterBot.service.Sender;
import skypro_ShelterBot.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static skypro_ShelterBot.enums.UserType.REGISTERED;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final UserService userService;
    private final AnimalService animalService;
    private final Sender sender;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(UserService userService, AnimalService animalService, Sender sender) {
        this.userService = userService;
        this.animalService = animalService;
        this.sender = sender;
    }

    @PostConstruct
    public void init() throws TelegramException {
        telegramBot.setUpdatesListener(this);

    }


    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
                    String messageText = update.message().text();
                    Long chatId = update.message().chat().id();
                    String regex = "отчет+(\\s+)(.+)";

                    if (messageText != null) {
                        logger.info("Processing update: {}", update.message().chat().id());

                        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(messageText.toLowerCase());


                        switch (messageText) {
                            case "/start" -> {
                                userService.autoCreateUserGuest(update);
                                sender.sendMassage(chatId, "Здравствуйте  , " + update.message().chat().firstName() + " , добро пожаловать в телеграм чат приюта домашних животных\n\n" +
                                        "Все пользователи проходят автоматическую регистрацию в базе приюта в роли Гостя");
                                break;
                            }
                            case "/help" -> {
                                sender.sendMassage(chatId, "Для работы с чат ботом воспользуйтесь командами \n\n" +
                                        "/start - Приветствие, начало работы\n\n" +
                                        "/cat_shelter - Выбрать приют для кошек \n\n" +
                                        "/dog_shelter -  Выбрать приют для собак \n\n" +
                                        "/cat_shelter_info - Получить информацию по приюту для кошек \n\n" +
                                        "/dog_shelter_info - Получить информацию по приюту для собак \n\n" +
                                        "/volunteer_info - Получить информацию о волонтере \n\n " +
                                        "/send_pet_report - Отправить отчет о питомце \n\n " +
                                        "/show_my_pets - Посмотреть питомцев на испытательном сроке");
                                break;
                            }
                            case "/cat_shelter" -> {
                                sender.sendMassage(chatId, "Приют для коше Лохматый хвост");
                                animalService.catsForAdoption(update);
                                break;
                            }
                            case "/dog_shelter" -> {
                                sender.sendMassage(chatId, "Приют для собак Мокрый нос");
                                animalService.dogsForAdoption(update);
                                break;
                            }
                            case "/cat_shelter_info" -> {
                                sender.sendMassage(chatId, "Приют для коше Лохматый хвост\n" +
                                        "Здесь вы найдете для себя четвероногих друзей\n" +
                                        "Адрес: Горбунковский р-н, Кошкина д, Четвероногих друзей ул, дом 1 \n" +
                                        "Телефон приюта 8 (818) 333 333 \n" +
                                        "Время работы: 9-21 без перерыва на обед, без выходных \n");
                                break;
                            }
                            case "/dog_shelter_info" -> {
                                sender.sendMassage(chatId, "Приют для собак Мокрый нос\n" +
                                        "Здесь вы найдете для себя четвероногих друзей\n" +
                                        "Адрес: Собачий р-н, Собачья д, Проспект Лающего пса, дом 11 \n" +
                                        "Телефон приюта 8 (821) 321 3321 \n" +
                                        "Время работы: 9-21 без перерыва на обед, без выходных \n");
                                break;
                            }
                            case "/volunteer_info" -> {
                                sender.sendMassage(chatId, "Служба поддержки, для оформления животных\n" +
                                        "Здесь вас проконсультируют опытные специалисты\n" +
                                        "Дадут более полный и развернутый ответ, на любой интересующий вас вопрос о животном \n" +
                                        "А также проведут процедуру усыновление  \n" +
                                        "Телефон службы волонтеров 8 (800) 000 0000 \n" +
                                        "Время работы: 9-21 без перерыва на обед, без выходных \n");
                                break;
                            }
                            case "/send_pet_report" -> {
                                sender.sendMassage(chatId, "Если Вы являетесь опекуном и хотите отправить ежедневный отчет о питомце,\n\n" +
                                        "Вам необходимо начать свое сообщение со слова отчет");

                                break;
                            }
                            case "/show_my_pets" -> {
                                animalService.showMyPets(update);
                                break;
                            }
                            default -> {
                                if (matcher.matches()) {
                                    userService.saveReport(matcher, chatId);
                                    logger.info("Отчет сохранен");
                                } else
                                    sender.sendMassage(chatId, "Для работы с ботом воспользуйтесь меню, либо введите команду /help");
                            }
                        }
                    }
                }
        );
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
