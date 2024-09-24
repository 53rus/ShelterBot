package skypro_ShelterBot.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skypro_ShelterBot.exception.UserNotFoundException;
import skypro_ShelterBot.exception.UserWithThisChatIdAlreadyExistException;
import skypro_ShelterBot.model.User;
import skypro_ShelterBot.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static skypro_ShelterBot.enums.UserType.GUEST;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void addUserTest() {
        User user = addTestUser();

        when(userRepository.save(user)).thenReturn(user);

        Assertions.assertNotNull(userService.findAll());
        Assertions.assertEquals(user, userService.addUser(user));
    }

    @Test
    public void findByChatIdTest() {
        User user = addTestUser();

        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));

        assertEquals(userService.findByChatId(user.getChatId()), user);
    }

    @Test
    public void editUserTest() {
        User user = addTestUser();

        when((userRepository.save(user))).thenReturn(user);

        when(userRepository.findByChatId(anyLong())).thenReturn(Optional.of(user));

        assertEquals(user, userService.editUser(user));
    }

    @Test
    public void deleteUserTest() {
        User user = addTestUser();

        when(userRepository.findByChatId(user.getChatId())).thenReturn(Optional.of(user));

        assertEquals(user, userService.deleteUser(user.getChatId()));
    }

    @Test
    public void getAllUserTest() {
        User user = addTestUser();
        List<User> userList = new ArrayList<>();
        userList.add(user);

        when(userRepository.findAll()).thenReturn(userList);

        assertEquals(userList, userService.findAll());
    }

    @Test
    public void UserNotFoundExceptionTest() {
        User user = addTestUser();

        when(userRepository.findByChatId(user.getChatId())).thenThrow(UserNotFoundException.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByChatId(user.getChatId()));
    }

    @Test
    public void UserWithThisChatIdAlreadyExistException() {
        User user = addTestUser();

        when(userRepository.findByChatId(user.getChatId())).thenThrow(UserWithThisChatIdAlreadyExistException.class);

        Assertions.assertThrows(UserWithThisChatIdAlreadyExistException.class, () -> userService.findByChatId(user.getChatId()));
    }


    private User addTestUser() {
        User user = new User();
        user.setChatId(10L);
        user.setFirstName("Alesha");
        user.setLastName("Popovich");
        user.setPhoneNumber("555");
        user.setAddress("Land");
        user.getUserType();
        return user;
    }


}
