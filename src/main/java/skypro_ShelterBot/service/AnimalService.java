package skypro_ShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.exception.AnimalNotFoundException;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.repository.AnimalRepository;
import skypro_ShelterBot.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public AnimalService(AnimalRepository animalRepository, UserRepository userRepository) {
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создание питомца в БД
     * <br>используется метод {@link JpaRepository#save(Object)}
     * @param animal
     */
    public Animal addAnimal(Animal animal) {
        animal.setId(null);
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

}
