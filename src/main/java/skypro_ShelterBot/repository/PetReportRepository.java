package skypro_ShelterBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import skypro_ShelterBot.model.PetReport;

import java.util.Collection;
import java.util.List;

@Repository
public interface PetReportRepository extends JpaRepository<PetReport,Long> {
    List<PetReport> findAllByChatId(Long chatId);
}
