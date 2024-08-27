package skypro_ShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.service.AnimalService;

import java.util.Collection;

@RestController
@RequestMapping("animal")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(
            summary = "Создание нового питомца В БД приюта",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры питомца,\n" +
                            "Поле GenderPet имеет строгие значения:\n" +
                            "FEMALE, MALE\n\n" +
                            "Поле ColorPet имеет строгие значения:\n" +
                            "WHITE, BLACK, BROWN, GREY, GINGER\n\n" +
                            "Поле ShelterType имеет строгие значения:\n" +
                            "CAT_SHELTER, DOG_SHELTER",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class)
                    )
            ),
            tags = "Питомец"

    )
    @PostMapping
    public ResponseEntity<Void> addAnimal(@RequestBody Animal animal) {
        animalService.addAnimal(animal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Показать всех питомцев приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                            )
                    )
            },
            tags = "Питомцы"
    )
    @GetMapping
    public ResponseEntity<Collection<Animal>> getAllAnimal() {
        Collection<Animal> animal = animalService.findAll();
        return ResponseEntity.ok(animal);
    }

    @Operation(
            summary = "Найти питомца по номеру Id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Запрашиваемый питомец не найден"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Cервер столкнулся с внутренней проблемой и не может нормально обработать запрос, отправленный клиентом."

                    )
            },
            tags = "Питомцы"
    )
    @GetMapping("/id")
    public ResponseEntity<Animal> getAnimalById(@RequestParam Long id) {
        Animal animal = animalService.findById(id);
        return ResponseEntity.ok(animal);
    }

    @Operation(
            summary = "Внести изменения в питомца",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры питомца,\n" +
                            "Поле GenderPet имеет строгие значения:\n" +
                            "FEMALE, MALE\n\n" +
                            "Поле ColorPet имеет строгие значения:\n" +
                            "WHITE, BLACK, BROWN, GREY, GINGER\n\n" +
                            "Поле ShelterType имеет строгие значения:\n" +
                            "CAT_SHELTER, DOG_SHELTER",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class))
            )

    )
    @PutMapping
    public ResponseEntity<Animal> editAnimal(@RequestBody Animal animal) {
        Animal editAnimal = animalService.editAnimal(animal);
        if (editAnimal == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editAnimal);
    }

    @Operation(
            summary = "Удалить питомца по номеру id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Запрашиваемый клиент не найден"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Cервер столкнулся с внутренней проблемой и не может нормально обработать запрос, отправленный клиентом."

                    )
            },
            tags = "Питомцы"
    )
    @DeleteMapping("{id}")
    public ResponseEntity<Animal> deleteAnimal(@PathVariable Long id) {
        Animal animal = animalService.deletAnimal(id);
        return ResponseEntity.ok(animal);
    }
}
