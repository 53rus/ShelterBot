package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skypro_ShelterBot.enums.ShelterType;
import skypro_ShelterBot.model.Animal;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>{
   List<Animal> findByShelterType(ShelterType shelterType);

   List<Animal> findAllByUserChatId(Long chatID);
}
