package last.project.lms.entities;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="tokens")
public class Tokens {

    @Id
    private String userId;

    private String token;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    public Tokens(String userId, String token, LocalDateTime expirationTime) {
        this.userId = userId;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public Tokens() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
