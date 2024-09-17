package skypro_ShelterBot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.exception.ReportsNotFoundException;
import skypro_ShelterBot.exception.UserNotFoundException;
import skypro_ShelterBot.exception.UserWithThisChatIdAlreadyExistException;
import skypro_ShelterBot.listener.TelegramBotUpdatesListener;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.PetReport;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.AnimalRepository;
import skypro_ShelterBot.repository.PetReportRepository;
import skypro_ShelterBot.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static skypro_ShelterBot.enums.UserType.*;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PetReportRepository petReportRepository;
    private final Sender sender;
    private final AnimalRepository animalRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PetReportRepository petReportRepository, Sender sender, AnimalRepository animalRepository) {
        this.userRepository = userRepository;
        this.petReportRepository = petReportRepository;
        this.sender = sender;
        this.animalRepository = animalRepository;
    }


    /**
     * Создание клиента в БД
     *
     * @param user <br>используется метод {@link JpaRepository#save(Object)}
     * @throws UserWithThisChatIdAlreadyExistException, выбрасывается когда пользователь с таким chatId уже существует в БД
     */
    public User addUser(User user) {
        Optional<User> saveUser = userRepository.findByChatId(user.getChatId());
        if (saveUser.isPresent()) {
            throw new UserWithThisChatIdAlreadyExistException();
        }
        user.setUserType(REGISTERED);
        return userRepository.save(user);
    }


    /**
     * Показать всех клиентов приюта
     * <br>используется метод {@link JpaRepository#findAll()}
     *
     * @return Collection Users
     */
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Поиск в user в БД
     * <br>используется метод {@link UserRepository#findByChatId(Long)}
     *
     * @param chatId
     * @return user
     */
    public User findByChatId(Long chatId) {
        Optional<User> user = userRepository.findByChatId(chatId);
        if (user.isPresent()) {
            return userRepository.findByChatId(chatId).get();
        }
        logger.error("User with chatId {} not found", chatId);
        throw new UserNotFoundException();
    }


    /**
     * Внесение изменений в существующего user
     * <br>используются методы {@link UserRepository#findByChatId(Long)}, {@link JpaRepository#save(Object)}
     *
     * @param user
     * @return user
     */
    public User editUser(User user) {
        Optional<User> editUser = userRepository.findByChatId(user.getChatId());
        if (editUser.isPresent()) {
            return userRepository.save(user);
        }
        logger.error("User user {} not found", user);
        throw new UserNotFoundException();
    }

    /**
     * Удаление user
     * <br>используется метод {@link JpaRepository#delete(Object)}
     *
     * @param chatId
     * @return deleteUser.get()
     */
    public User deleteUser(Long chatId) {
        Optional<User> deleteUser = userRepository.findByChatId(chatId);
        if (deleteUser.isPresent()) {
            userRepository.deleteById(chatId);
            return deleteUser.get();
        }
        logger.error("User to be removed with id {} not found", chatId);
        throw new UserNotFoundException();
    }

    /**
     * Создание user со значением поля userType = GUEST
     * <br>используются методы {@link UserRepository#findByChatId(Long)}, {@link JpaRepository#save(Object)}
     *
     * @param update
     */
    public void autoCreateUserGuest(Update update) {
        Long chatId = update.message().chat().id();

        Optional<User> user = userRepository.findByChatId(chatId);
        if (user.isEmpty()) {
            User guest = new User();
            guest.setChatId(update.message().chat().id());
            guest.setFirstName(update.message().chat().firstName());
            guest.setLastName(update.message().chat().lastName());
            guest.setUserType(GUEST);
            guest.setAddress(null);
            guest.setPhoneNumber(null);

            userRepository.save(guest);
        }
    }

    /**
     * Метод по сохранению пользовательского отчета в БД
     * @param matcher
     * @param chatId
     */

    public void saveReport(Matcher matcher, Long chatId) {
        PetReport petReport = new PetReport();
        User user = findByChatId(chatId);
        List<Animal> animals = animalRepository.findAllByUserChatId(user.getChatId());
        String text = matcher.group(2);
        petReport.setMessageText(text);
        petReport.setChatId(chatId);

        if (user.getUserType() == ADOPTER && !animals.isEmpty()) {
            petReportRepository.save(petReport);
            sender.sendMassage(chatId, "Отчет доставлен");
        } else {
            sender.sendMassage(chatId, "У Вас нет питомцев для отчетности");
        }
    }

    public List<PetReport> findAllReportsByUserChatId(Long chatId) {
        List<PetReport> reports= petReportRepository.findAllByChatId(chatId);
        if (reports.isEmpty()) {
            logger.info("Reports not found from user by ChatId {}", chatId);
            throw new ReportsNotFoundException();
        }
        return reports;
    }
}
