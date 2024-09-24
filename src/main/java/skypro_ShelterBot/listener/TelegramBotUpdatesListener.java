package skypro_ShelterBot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.service.AnimalService;
import skypro_ShelterBot.service.Sender;
import skypro_ShelterBot.service.UserService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static skypro_ShelterBot.constants.Const.*;

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
                    String regex = "((report)(\\s)(\\d+)(\\s)(.++))";

                    if (messageText != null) {
                        logger.info("Processing update: {}", update.message().chat().id());

                        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(messageText);




                        switch (messageText) {
                            case "/start" -> {
                                userService.autoCreateUserGuest(update);
                                sender.sendMassage(chatId, "Здравствуйте  , " + update.message().chat().firstName() + " , добро пожаловать в телеграм чат приюта домашних животных\n\n" +
                                        "Все пользователи проходят автоматическую регистрацию в базе приюта в роли Гостя");
                                break;
                            }
                            case "/help" -> {
                                sender.sendMassage(chatId, HELP);
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
                                sender.sendMassage(chatId, CAT_SHELTER_INFO + "https://fabrikakovki.ru/userfiles/service/Chemodanovka.jpg");
                                break;
                            }
                            case "/dog_shelter_info" -> {
                                sender.sendMassage(chatId, DOG_SHELTER_INFO + "https://avatars.mds.yandex.net/i?id=62e0da82b1aef52b79d63d00453e0525_l-5233787-images-thumbs&n=13");
                                break;
                            }
                            case "/volunteer_info" -> {
                                sender.sendMassage(chatId, VOLUNTEER_INFO);
                                break;
                            }
                            case "/send_pet_report" -> {
                                sender.sendMassage(chatId, "Если Вы являетесь опекуном и хотите отправить ежедневный отчет о питомце,\n\n" +
                                        "Вам необходимо начать свое сообщение со слова report id питомца.\n\n" +
                                        "ID питомца можно посмотреть тут /show_my_pets");
                                break;
                            }
                            case "/show_my_pets" -> {
                                animalService.showMyPets(update);
                                break;
                            }
                            default -> {
                                if (matcher.matches()) {
                                    userService.saveReport(matcher, chatId);
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
