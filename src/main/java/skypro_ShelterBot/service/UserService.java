package skypro_ShelterBot.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.exception.UserNotFoundException;
import skypro_ShelterBot.exception.UserWithThisChatIdAlreadyExistException;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

import static skypro_ShelterBot.enums.UserType.GUEST;
import static skypro_ShelterBot.enums.UserType.REGISTERED;

@Service
public class UserService {


    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Создание клиента в БД
     * @param user
     * <br>используется метод {@link JpaRepository#save(Object)}
     * @throws UserWithThisChatIdAlreadyExistException, выбрасывается когда пользователь с таким chatId уже существует в БД
     */
    public void addUser(User user) {
        Optional<User> saveUser = userRepository.findByChatId(user.getChatId());
        if (saveUser.isPresent()) {
            throw new UserWithThisChatIdAlreadyExistException();
        }
        user.setUserType(REGISTERED);
        userRepository.save(user);
    }


    /**
     * Показать всех клиентов приюта
     * <br>используется метод {@link JpaRepository#findAll()}
     * @return Collection Users
     */
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Поиск в user в БД
     *<br>используется метод {@link UserRepository#findByChatId(Long)}
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
     * Внесение изменеией в существующего user
     * <br>используются методы {@link UserRepository#findByChatId(Long)}, {@link JpaRepository#save(Object)}
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
     *  <br>используется метод {@link JpaRepository#delete(Object)}
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


}
