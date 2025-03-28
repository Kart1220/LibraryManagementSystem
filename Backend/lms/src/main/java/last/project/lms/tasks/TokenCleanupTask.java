package last.project.lms.tasks;

import last.project.lms.repo.TokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {

    @Autowired
    private TokenRepo tokenRepository;

    // Scheduled task to delete expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 milliseconds
    public void deleteExpiredTokens() {
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(1);
        tokenRepository.deleteByExpirationTimeBefore(thresholdTime);
    }
}

