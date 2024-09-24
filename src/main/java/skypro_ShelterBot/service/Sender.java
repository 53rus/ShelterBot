package skypro_ShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.AnimalRepository;
import skypro_ShelterBot.repository.UserRepository;

import java.util.List;

@Component
public class Sender {
    private final TelegramBot telegramBot;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    public Sender(TelegramBot telegramBot, AnimalRepository animalRepository, UserRepository userRepository) {
        this.telegramBot = telegramBot;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Отправка сообщений в тг
     * @param chatId
     * @param answer
     */
    public void sendMassage(Long chatId, String answer) {
        SendMessage message = new SendMessage(chatId, answer);
        SendResponse response = telegramBot.execute(message);
    }

    /**
     * Отправка сообщений в тг, напоминаний, о необходимости отправить отчет
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void send() {
        List<Animal> animals = animalRepository.findAll()
                .stream()
                .filter(animal -> animal.getProbation()!=null)
                .toList();
        animals.forEach(animal -> {
                sendMassage(animal.getUser().getChatId(), "Незабываем отправить отчет о питомце  "
                        + animal.getNamePet() +
                        "\nКак отправить отчет смотрите тут /send_pet_report");
        });
    }
}
