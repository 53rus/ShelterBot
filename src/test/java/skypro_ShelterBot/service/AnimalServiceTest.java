package skypro_ShelterBot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skypro_ShelterBot.exception.AnimalNotFoundException;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.repository.AnimalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static skypro_ShelterBot.enums.ColorPet.WHITE;
import static skypro_ShelterBot.enums.ShelterType.DOG_SHELTER;

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @Mock
    private AnimalRepository animalRepository;
    @InjectMocks
    private AnimalService animalService;

    @Test
    public void addAnimalTest() {
        Animal animal = addTestAnimal();

        when(animalRepository.save(animal)).thenReturn(animal);

        Assertions.assertNotNull(animalService.findAll());
        Assertions.assertEquals(animal, animalService.addAnimal(animal));
    }

    @Test
    public void findByIdTest() {
        Animal animal = addTestAnimal();

        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(animal));

        assertEquals(animalService.findById(animal.getId()), animal);
    }

    @Test
    public void editAnimalTest() {
        Animal animal = addTestAnimal();

        when((animalRepository.save(animal))).thenReturn(animal);

        when(animalRepository.findById(anyLong())).thenReturn(Optional.of(animal));

        assertEquals(animal, animalService.editAnimal(animal));
    }

    @Test
    public void deleteAnimalTest() {
        Animal animal = addTestAnimal();

        when(animalRepository.findById(animal.getId())).thenReturn(Optional.of(animal));

        assertEquals(animal, animalService.deletAnimal(animal.getId()));
    }

    @Test
    public void getAllAnimalTest() {
        Animal animal = addTestAnimal();
        List<Animal> animalList = new ArrayList<>();
        animalList.add(animal);

        when(animalRepository.findAll()).thenReturn(animalList);

        assertEquals(animalList, animalService.findAll());
    }

    @Test
    public void AnimalNotFoundExceptionTest() {
        Animal animal = addTestAnimal();

        when(animalRepository.findById(animal.getId())).thenThrow(AnimalNotFoundException.class);

        Assertions.assertThrows(AnimalNotFoundException.class, () -> animalService.findById(animal.getId()));
    }

    private Animal addTestAnimal() {
        Animal animal = new Animal();
        animal.setId(444L);
        animal.setNamePet("No1");
        animal.setAge(10);
        animal.setColorPet(WHITE);
        animal.setShelterType(DOG_SHELTER);
        return animal;
    }
}


