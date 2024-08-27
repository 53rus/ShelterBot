package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skypro_ShelterBot.model.Animal;

public interface AnimalRepository extends JpaRepository<Animal, Long>{
}
