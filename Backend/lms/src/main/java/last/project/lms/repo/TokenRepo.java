package last.project.lms.repo;

import last.project.lms.entities.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenRepo extends JpaRepository<Tokens, Long> {
    void deleteByExpirationTimeBefore(LocalDateTime thresholdTime);
    Tokens findByToken(String token);

}
