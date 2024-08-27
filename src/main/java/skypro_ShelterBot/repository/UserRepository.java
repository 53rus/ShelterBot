package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skypro_ShelterBot.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByChatId(Long chatId);
}
