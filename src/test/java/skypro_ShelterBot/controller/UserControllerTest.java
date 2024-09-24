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
import skypro_ShelterBot.model.User;


import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static skypro_ShelterBot.enums.UserType.GUEST;
import static skypro_ShelterBot.enums.UserType.REGISTERED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private UserController userController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void addUserTest() throws Exception {
        User user = addTestUser();

        ResponseEntity<User> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/user", user, User.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(response.getBody()).getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(response.getBody().getLastName()).isEqualTo(user.getLastName());
    }


    @Test
    public void getUserByIdTest() throws Exception {

        User user = addTestUser();

        ResponseEntity<User> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/user", user, User.class);

        ResponseEntity<User> responseEntity = testRestTemplate
                .getForEntity("http://localhost:" + port + "/user/" + response.getBody().getChatId(), User.class);

        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getBody()).isNotNull();
        Assertions.assertThat(responseEntity.getBody().getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(responseEntity.getBody().getLastName()).isEqualTo(user.getLastName());
    }


    @Test
    public void editUserTest() throws Exception{

        User user = addTestUser();

        ResponseEntity<User> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/user", user, User.class);

        User editUser = editUser();

        editUser.setChatId(response.getBody().getChatId());

        ResponseEntity<User> newResponse = testRestTemplate
                .exchange("http://localhost:" + port + "/user", HttpMethod.PUT, new HttpEntity<>(editUser), User.class);

        assertThat(newResponse.getBody().getFirstName()).isEqualTo(editUser.getFirstName());
        assertThat(newResponse.getBody().getLastName()).isEqualTo(editUser.getLastName());
        assertThat(newResponse.getBody().getAddress()).isEqualTo(editUser.getAddress());
    }


    @Test
    public void deleteUserTest() throws Exception {

        User user = addTestUser();

        ResponseEntity<User> response = testRestTemplate
                .postForEntity("http://localhost:" + port + "/user", user, User.class);

        ResponseEntity<Void> responseEntity = testRestTemplate.exchange("http://localhost:" + port + "/user/" + response.getBody().getChatId(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        assertNull(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getAllUserTest() throws Exception {

        User user = addTestUser();

        User response = testRestTemplate
                .postForObject("http://localhost:" + port + "/user", user, User.class);


        List listUser = testRestTemplate.getForObject("http://localhost:" + port + "/user", List.class);

        assertThat(listUser).isNotNull();
        assertThat(listUser.contains(response.getFirstName()));
        assertThat(listUser.contains(response.getLastName()));
        assertThat(listUser.contains(response.getChatId()));
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

    private User editUser() {
        User user = new User();
        user.setChatId(11L);
        user.setFirstName("Fedka");
        user.setLastName("Trinkin");
        user.setPhoneNumber("666");
        user.setAddress("Hop");
        user.getUserType();
        return user;
    }
}
