package skypro_ShelterBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import skypro_ShelterBot.exception.AnimalNotFoundException;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.repository.AnimalRepository;

import java.util.Collection;
import java.util.Optional;
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public void addAnimal(Animal animal) {
        animal.setId(null);
        animalRepository.save(animal);
    }

    public Collection<Animal> findAll() {
        return animalRepository.findAll();
    }

    public Animal findById(Long id) {
        Optional<Animal> animal = animalRepository.findById(id);
        if (animal.isPresent()) {
            return animalRepository.findById(id).get();
        }
        logger.error("Animal with id {} not found", id);
        throw new AnimalNotFoundException();
    }

    public Animal editAnimal(Animal animal) {
        Optional<Animal> editAnimal = animalRepository.findById(animal.getId());
        if (editAnimal.isPresent()) {
            return animalRepository.save(animal);
        }
        logger.error("Animal animal {} not found", animal);
        throw new AnimalNotFoundException();
    }

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
