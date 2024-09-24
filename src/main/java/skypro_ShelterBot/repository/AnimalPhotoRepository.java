package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skypro_ShelterBot.model.AnimalPhoto;

import java.util.Optional;
@Repository
public interface AnimalPhotoRepository extends JpaRepository<AnimalPhoto, Long> {
    Optional<AnimalPhoto> findByAnimalId(Long animalId);
}
