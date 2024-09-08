package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skypro_ShelterBot.model.AnimalPhoto;

import java.util.Optional;

public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Long> {
    Optional<AnimalPhoto> findByAnimalId(Long animalId);
}
