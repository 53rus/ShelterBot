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
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Создание нового клинта В БД приюта",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры клиента,\n" +
                            "поле UserType имеет значения:\n" +
                            "REGISTERED\n" +
                            "    GUEST\n" +
                            "    ADOPTER",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class)
                    )
            ),
            tags = "Клиент"

    )
    @PostMapping
    public ResponseEntity<Void> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Показать всех клиентов приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Запрос выполнен успешно",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = User.class))
                            )
                    )
            },
            tags = "Клиенты"
    )
    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        Collection<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Найти клинта по номеру chatId",
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
            tags = "Клиенты"
    )
    @GetMapping("/chatId")
    public ResponseEntity<User> getUserByChatId(@RequestParam Long chatId) {
        User user = userService.findByChatId(chatId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Внести изменения в клиента",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры клиента,\n" +
                            "поле UserType имеет значения:\n" +
                            "REGISTERED\n" +
                            "   GUEST\n" +
                            "   ADOPTER",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = User.class))
            )

    )
    @PutMapping
    public ResponseEntity<User> editUser(@RequestBody User user) {
        User editUser = userService.editUser(user);
        if (editUser == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editUser);
    }

    @Operation(
            summary = "Удалить клиента по номеру chatId",
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
            tags = "Клиенты"
    )
    @DeleteMapping("{chatId}")
    public ResponseEntity<User> deleteUser(@PathVariable Long chatId) {
        User user = userService.deleteUser(chatId);
        return ResponseEntity.ok(user);
    }
}
