package skypro_ShelterBot.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import skypro_ShelterBot.model.Animal;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static skypro_ShelterBot.enums.ColorPet.BROWN;
import static skypro_ShelterBot.enums.ColorPet.WHITE;
import static skypro_ShelterBot.enums.ShelterType.CAT_SHELTER;
import static skypro_ShelterBot.enums.ShelterType.DOG_SHELTER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnimalControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private AnimalController animalController;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    void addAnimalTest() throws Exception {
        Animal animal = addTestAnimal();

        ResponseEntity<Animal> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/animal", animal, Animal.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(response.getBody()).getNamePet()).isEqualTo(animal.getNamePet());
        Assertions.assertThat(response.getBody().getAge()).isEqualTo(animal.getAge());
    }


    @Test
    public void getAnimalByIdTest() throws Exception {

        Animal animal = addTestAnimal();

        ResponseEntity<Animal> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/animal", animal, Animal.class);

        ResponseEntity<Animal> responseEntity = testRestTemplate
                .getForEntity("http://localhost:" + port + "/animal/" + response.getBody().getId(), Animal.class);

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody().getAge()).isEqualTo(animal.getAge());
        Assertions.assertThat(responseEntity.getBody().getNamePet()).isEqualTo(animal.getNamePet());
    }


    @Test
    public void editAnimalTest() throws Exception{

        Animal animal = addTestAnimal();

        ResponseEntity<Animal> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/animal", animal, Animal.class);

        Animal editAnima = editTestAnimal();

        editAnima.setId(response.getBody().getId());

        ResponseEntity<Animal> newResponse = testRestTemplate
                .exchange("http://localhost:" + port + "/animal", HttpMethod.PUT, new HttpEntity<>(editAnima), Animal.class);

        assertThat(newResponse.getBody().getAge()).isEqualTo(editAnima.getAge());
        assertThat(newResponse.getBody().getNamePet()).isEqualTo(editAnima.getNamePet());
        assertThat(newResponse.getBody().getColorPet()).isEqualTo(editAnima.getColorPet());
    }


    @Test
    public void deleteAnimalTest() throws Exception {

        Animal animal = addTestAnimal();

        ResponseEntity<Animal> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/animal", animal, Animal.class);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/animal/" + response.getBody().getId(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertNull(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAllAnimalTest() throws Exception {

        Animal animal = addTestAnimal();

        Animal response = testRestTemplate
                .postForObject("http://localhost:" + port + "/student", animal, Animal.class);


        List listAnimal = testRestTemplate.getForObject("http://localhost:" + port + "/animal", List.class);

        assertThat(listAnimal).isNotNull();
        assertThat(listAnimal.contains(response.getAge()));
        assertThat(listAnimal.contains(response.getNamePet()));
        assertThat(listAnimal.contains(response.getId()));
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


    private Animal editTestAnimal() {
        Animal animal = new Animal();
        animal.setId(555L);
        animal.setNamePet("No2");
        animal.setAge(11);
        animal.setColorPet(BROWN);
        animal.setShelterType(CAT_SHELTER);
        return animal;
    }
}
