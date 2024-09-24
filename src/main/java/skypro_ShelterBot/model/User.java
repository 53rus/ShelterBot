package skypro_ShelterBot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import skypro_ShelterBot.enums.UserType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table (name = "users")
public class User {
    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    @CreationTimestamp
    private LocalDateTime registrationDate;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Collection<Animal> animals;


    public User(Long chatId, String firstName, String lastName, String phoneNumber, String address, LocalDateTime registrationDate, UserType userType, Collection<Animal> animals) {
        this.chatId = chatId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.registrationDate = registrationDate;
        this.userType = userType;
        this.animals = animals;
    }

    public User() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public Collection<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(Collection<Animal> animals) {
        this.animals = animals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(chatId, user.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chatId);
    }
}
