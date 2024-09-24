package skypro_ShelterBot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import skypro_ShelterBot.model.Animal;
import skypro_ShelterBot.model.AnimalPhoto;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.service.AnimalPhotoService;
import skypro_ShelterBot.service.AnimalService;
import skypro_ShelterBot.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("animal")
public class AnimalController {

    private final AnimalService animalService;
    private final UserService userService;
    private final AnimalPhotoService animalPhotoService;

    public AnimalController(AnimalService animalService, UserService userService, AnimalPhotoService animalPhotoService) {
        this.animalService = animalService;
        this.userService = userService;
        this.animalPhotoService = animalPhotoService;
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
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal) {
        Animal saveAnimal = animalService.addAnimal(animal);
        if (saveAnimal == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(animal);
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

    @GetMapping("{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable Long id) {
        Animal animal = animalService.findById(id);
        if (animal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
            ),
            tags = "Питомцы"
    )

    @PutMapping
    public ResponseEntity<Animal> editAnimal(@RequestBody Animal animal) {
        Animal editAnimal = animalService.editAnimal(animal);
        if (editAnimal == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editAnimal);
    }

    @Operation(summary = "Назначить опекуна питомцу",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Опекун назначен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Animal.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Питомец уже нашел своего хозяина"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Пользователь не прошел полную регистрацию"
                    )
            },
            tags = "Питомцы"
    )

    @PostMapping(path = "/animal/add-adopter")
    public ResponseEntity<Animal> assignAPetToAnAdopter(@Parameter(description = "ChatId усыновителя")
                                                        @RequestParam Long chatId,
                                                        @Parameter(description = "ID питомца")
                                                        @RequestParam Long id) {
        Animal animal = animalService.findById(id);
        User user = userService.findByChatId(chatId);

        if (user != null && animal != null) {
            animalService.addAdopter(user, animal);
        }

        return ResponseEntity.ok(animal);
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
    public ResponseEntity<Animal> deleteAnimal(@Parameter(description = "Id питомца")
                                               @PathVariable Long id) {
        Animal animal = animalService.deletAnimal(id);
        return ResponseEntity.ok(animal);
    }

    @Operation(
            summary = "Присвоить фото питомцу",
            description = "Выберите питомца по Id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Выберите фото",
                    extensions = {@Extension(
                            properties = {})}
            ),
            tags = "Питомцы"
    )

    @PostMapping(value = "{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCover(@PathVariable Long id, @RequestParam MultipartFile photo) throws IOException {
        if (photo.getSize() >= 1200 * 628) {
            return ResponseEntity.badRequest().body("File is too big");
        }
        animalPhotoService.uploadPhoto(id, photo);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Посмотреть фото по ID питомца",
            description = "Выберите питомца по Id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    extensions = {@Extension(
                            properties = {})}
            ),
            tags = "Питомцы"
    )

    @GetMapping(value = "{id}/photo")
    public void downloadCover(@PathVariable Long id, HttpServletResponse response) throws IOException {
        AnimalPhoto photo = animalPhotoService.findAnimalPhoto(id);

        Path path = Path.of(photo.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(photo.getMediaType());
            response.setContentLength((int) photo.getFileSize());
            is.transferTo(os);
        }
    }

    @Operation(
            summary = "Изменить срок опекунства питомцу",
            description = "Установите новый срок опекунства для питомца",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    extensions = {@Extension(
                            properties = {})}
            ),
            tags = "Питомцы"
    )
    @PutMapping("/{id},{probation}")
    public ResponseEntity<Animal> changeAnimalProbationPeriod(@Parameter (description = "ID питомца")@PathVariable Long id,
                                                              @Parameter(description = "Новый срок опекунства в днях") @PathVariable Integer probation) {
        Animal animal = animalService.changeAnimalProbationPeriod(id, probation);
        if (animal == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
            return ResponseEntity.ok(animal);
    }
}
