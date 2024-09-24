package skypro_ShelterBot.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.exception.ReportsNotFoundException;
import skypro_ShelterBot.exception.UserNotFoundException;
import skypro_ShelterBot.exception.UserWithThisChatIdAlreadyExistException;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.PetReport;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.AnimalRepository;
import skypro_ShelterBot.repository.PetReportRepository;
import skypro_ShelterBot.repository.UserRepository;

import java.time.LocalDate;
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
    private final AnimalService animalService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PetReportRepository petReportRepository, Sender sender, AnimalRepository animalRepository, AnimalService animalService) {
        this.userRepository = userRepository;
        this.petReportRepository = petReportRepository;
        this.sender = sender;
        this.animalRepository = animalRepository;
        this.animalService = animalService;
    }


    /**
     * Создание клиента в БД
     *
     * @param user <br>используется метод {@link JpaRepository#save(Object)}
     * @throws UserWithThisChatIdAlreadyExistException, выбрасывается когда пользователь с таким chatId уже существует в БД
     */
    public User addUser(User user) {
        if (userRepository.findByChatId(user.getChatId()).isPresent()) {
            logger.info("Пользователь chatId {} уже существует", + user.getChatId());
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
     * <br>используются методы
     * {@link AnimalRepository#findAllByUserChatId(Long)},
     * {@link AnimalRepository#save(Object)},
     * {@link PetReportRepository#save(Object)}
     *
     * @param matcher
     * @param chatId
     */

    public void saveReport(Matcher matcher, Long chatId) {
        LocalDate date = LocalDate.now();
        PetReport petReport = new PetReport();

        String text = matcher.group(6);
        long animalId = Long.parseLong(matcher.group(4));

        List<Animal> animals = animalRepository.findAllByUserChatId(chatId)
                .stream()
                .filter(animal -> animal.getId().equals(animalId) && animal.getProbation() != null)
                .toList();
        if (animals.isEmpty()) {
            sender.sendMassage(chatId, "Питомца с ID " + animalId + " на испытательном сроке закрепленном за Вами, не найдено\n\n" +
                    "ID усыновленных питомцев на испытательном сроке можно посмотреть тут /show_my_pets ");
        } else {
            List<PetReport> reports = petReportRepository.findByAnimalId(animalId)
                    .stream()
                    .filter(report -> (report.getDateTime().toLocalDate()).equals(date))
                    .toList();
            if (!reports.isEmpty()) {
                sender.sendMassage(chatId, "Отчет по данному питомцу уже отправлялся:  " + date);
            } else animals.forEach(animal -> {
                petReport.setMessageText(text);
                petReport.setChatId(chatId);
                petReport.setAnimal(animal);
                petReportRepository.save(petReport);
                logger.info("Отчет сохранен");

                animal.setProbation(animal.getProbation() - 1);
                logger.info("Срок опекунства изменен {}", animal.getProbation());
                animalRepository.save(animal);

                if (animal.getProbation() == 0) {
                    animal.setProbation(null);
                    logger.info("Срок опекунства закончился {}", animal.getProbation());
                    sender.sendMassage(chatId, "Поздравляем, испытательный срок в роли усыновителя для питомца " + animal.getNamePet() + " закончился");
                    animalRepository.save(animal);
                }
                sender.sendMassage(chatId, "Отчет доставлен");
            });
        }

    }

    /**
     * Метод по выводу доставленных пользователя отчетов
     * * @param chatId
     *
     * @return reports (список отчетов)
     */
    public List<PetReport> findAllReportsByUserChatId(Long chatId) {
        List<PetReport> reports = petReportRepository.findAllByChatId(chatId);
        if (reports.isEmpty()) {
            logger.info("Reports not found from user by ChatId {}", chatId);
            throw new ReportsNotFoundException();
        }
        return reports;
    }

    /**
     * Метотод отправки сообщений в тг из браузера
     *
     * @param chatId
     * @param message
     */
    public void sendMessageToUser(Long chatId, String message) {
        sender.sendMassage(chatId, message);
    }
}
