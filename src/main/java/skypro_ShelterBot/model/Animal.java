package skypro_ShelterBot.model;

import jakarta.persistence.*;
import skypro_ShelterBot.enums.ColorPet;
import skypro_ShelterBot.enums.GenderPet;
import skypro_ShelterBot.enums.ShelterType;

import java.util.Objects;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namePet;
    private double age;
    @Enumerated(EnumType.STRING)
    private GenderPet genderPet;
    @Enumerated(EnumType.STRING)
    private ColorPet colorPet;
    @Enumerated(EnumType.STRING)
    private ShelterType shelterType;

    public Animal(Long id, String namePet, double age, GenderPet genderPet, ColorPet colorPet, ShelterType shelterType) {
        this.id = id;
        this.namePet = namePet;
        this.age = age;
        this.genderPet = genderPet;
        this.colorPet = colorPet;
        this.shelterType = shelterType;
    }

    public Animal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamePet() {
        return namePet;
    }

    public void setNamePet(String namePet) {
        this.namePet = namePet;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public GenderPet getGenderPet() {
        return genderPet;
    }

    public void setGenderPet(GenderPet genderPet) {
        this.genderPet = genderPet;
    }

    public ColorPet getColorPet() {
        return colorPet;
    }

    public void setColorPet(ColorPet colorPet) {
        this.colorPet = colorPet;
    }

    public ShelterType getShelterType() {
        return shelterType;
    }

    public void setShelterType(ShelterType shelterType) {
        this.shelterType = shelterType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return Objects.equals(id, animal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

