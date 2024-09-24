package skypro_ShelterBot.service;

import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.enums.ShelterType;
import skypro_ShelterBot.enums.UserType;
import skypro_ShelterBot.exception.AnimalAlreadyHasOwnerException;
import skypro_ShelterBot.exception.AnimalNotFoundException;
import skypro_ShelterBot.exception.UserDidNotCompleteFullRegistrationException;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.AnimalRepository;
import skypro_ShelterBot.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static skypro_ShelterBot.constants.Const.*;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final Sender sender;
    Logger logger = LoggerFactory.getLogger(Animal.class);

    public AnimalService(AnimalRepository animalRepository, UserRepository userRepository, Sender sender) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
        this.sender = sender;
    }

    /**
     * Создание питомца в БД
     * <br>используется метод {@link JpaRepository#save(Object)}
     *
     * @param animal
     */
    public Animal addAnimal(Animal animal) {
        animal.setId(null);
        animal.setProbation(null);
        return animalRepository.save(animal);
    }

    /**
     * Показать всех питомцев приюта
     * <br>используется метод {@link JpaRepository#findAll()}
     * * @return {@link Collection<Animal>}
     */
    public Collection<Animal> findAll() {
        return animalRepository.findAll();
    }

    /**
     * Найти питомца по id
     * <br>используется метод {@link JpaRepository#findById(Object)}
     *
     * @param id
     * @return animal
     */
    public Animal findById(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        if (animal.isPresent()) {
            return animalRepository.findById(id).get();
        }
        logger.error("Animal with id {} not found", id);
        throw new AnimalNotFoundException();
    }

    /**
     * Внесение изменений в запись animal
     * <br>используется метод {@link JpaRepository#findById(Object),JpaRepository#save(Object)}
     *
     * @param animal
     * @return animal
     */
    public Animal editAnimal(Animal animal) {
        Optional<Animal> editAnimal = animalRepository.findById(animal.getId());
        if (editAnimal.isPresent()) {
            return animalRepository.save(animal);
        }
        logger.error("Animal animal {} not found", animal);
        throw new AnimalNotFoundException();
    }

    /**
     * Удаление animal из БД
     * <br>используется метод {@link JpaRepository#findById(Object),JpaRepository#delete(Object)}
     *
     * @param id
     * @return animal
     */

    public Animal deletAnimal(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);

        if (animal.isPresent()) {
            animalRepository.deleteById(id);
            return animal.get();
        }

        logger.error("Animal to be removed with id {} not found", id);

        throw new AnimalNotFoundException();
    }

    /**
     * Присвоить питомцу (animal) опекуна (user)
     * <br>используется метод {@link JpaRepository#findById(Object)}
     *
     * @param user, animal
     * @return animal
     */
    public void addAdopter(User user, Animal animal) {

        if (animal.getUser() != null) {
            logger.error("The animal id {} already has an adopter chatId {}", animal.getId(), animal.getUser().getChatId());

            throw new AnimalAlreadyHasOwnerException();

        }
        if (user.getUserType() == UserType.GUEST) {
            logger.error("User chatId {} has not completed full registration ", user.getChatId());

            throw new UserDidNotCompleteFullRegistrationException();
        }

        animal.setUser(user);
        animal.setProbation(30);
        animalRepository.save(animal);
        user.setUserType(UserType.ADOPTER);
        userRepository.save(user);

        logger.info("The user chatID {} has successfully completed the pet ID {} adoption procedure ", user.getChatId(), animal.getId());

        if (animal.getShelterType() == ShelterType.CAT_SHELTER) {
            sender.sendMassage(user.getChatId(), ADOPTER_CAT + animal.getNamePet() + WISHES);
            sender.sendMassage(user.getChatId(), RECOMMENDATIONS_FOR_CAT);
        } else {
            sender.sendMassage(user.getChatId(), ADOPTER_DOG + animal.getNamePet() + WISHES);
            sender.sendMassage(user.getChatId(), RECOMMENDATIONS_FOR_DOG);
        }
    }

    /**
     * Вывести доступных питомцев в телеграм по кошачьему приюту
     *
     * @param update
     */

    public void catsForAdoption(Update update) {
        List<Animal> cats = animalRepository.findByShelterType(ShelterType.CAT_SHELTER)
                .stream()
                .filter(cat -> cat.getUser() == null).toList();

        if (cats.isEmpty()) {
            sender.sendMassage(update.message().chat().id(), "В данный момент котиков ищущих хозяина нет");
        } else {
            sender.sendMassage(update.message().chat().id(), "Котики нашего приюта :");
            cats.forEach(cat -> {
                sender.sendMassage(update.message().chat().id(),
                        "ID питомца: " + cat.getId() + ",  Кличка: " + cat.getNamePet() +
                                ",  Возраст: " + cat.getAge() + ",  Пол: " + cat.getGenderPet() +
                                ",  Окрас: " + cat.getColorPet());
            });
        }
    }

    /**
     * Вывести доступных питомцев в телеграм по собачьему приюту
     *
     * @param update
     */

    public void dogsForAdoption(Update update) {
        List<Animal> dogs = animalRepository.findByShelterType(ShelterType.DOG_SHELTER)
                .stream()
                .filter(dog -> dog.getUser() == null)
                .toList();

        if (dogs.isEmpty()) {
            sender.sendMassage(update.message().chat().id(), "В данный момент песиков ищущих хозяина нет");
        } else {
            sender.sendMassage(update.message().chat().id(), "Песики нашего приюта :");
            dogs.forEach(dog -> {
                sender.sendMassage(update.message().chat().id(),
                        "ID питомца: " + dog.getId() + ",  Кличка: " + dog.getNamePet() +
                                ",  Возраст: " + dog.getAge() + ",  Пол: " + dog.getGenderPet() +
                                ",  Окрас: " + dog.getColorPet());
            });
        }
    }

    /**
     * Вывести в телеграм питомцев клиента на испытательном сроке
     *
     * @param update
     */
    public void showMyPets(Update update) {
        List<Animal> animals = animalRepository.findAllByUserChatId(update.message().chat().id())
                .stream()
                .filter(animal -> animal.getProbation() != null)
                .toList();

        if (animals.isEmpty()) {
            sender.sendMassage(update.message().chat().id(), "В данный момент у вас нет питомцев на испытательном сроке");
        } else {
            sender.sendMassage(update.message().chat().id(), "Ваши питомцы на испытательном сроке:  ");
            animals.forEach(animal -> {
                        sender.sendMassage(update.message().chat().id(),
                                "ID питомца: " + animal.getId() +
                                        ",  Кличка: " + animal.getNamePet() +
                                        ",  Возраст: " + animal.getAge() +
                                        ",  Пол: " + animal.getGenderPet() +
                                        ",  Окрас: " + animal.getColorPet() +
                                        ", Испытательный срок:  " + animal.getProbation());
                    }
            );
        }
    }

    /**
     * Изменить срок опекунства
     * @param id
     * @param probation
     * @return
     */
    public Animal changeAnimalProbationPeriod(Long id, Integer probation) {
        Animal animal = findById(id);
        if (animal != null) {
            animal.setProbation(probation);
            return animalRepository.save(animal);
        }
        throw new AnimalNotFoundException();
    }
}
