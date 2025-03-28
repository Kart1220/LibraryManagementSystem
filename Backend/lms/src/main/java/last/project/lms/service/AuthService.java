package last.project.lms.service;

import last.project.lms.entities.Tokens;
import last.project.lms.entities.Users;
import last.project.lms.exceptions.InvalidAuthException;
import last.project.lms.repo.TokenRepo;
import last.project.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenRepo tokenRepo;

    public Tokens login(String userId, String password) throws InvalidAuthException {
        Optional<Users> user = userRepo.findById(userId);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                Tokens tokens = new Tokens(userId, UUID.randomUUID().toString(), LocalDateTime.now());
                tokenRepo.save(tokens);
                return tokens;
            }
            throw new InvalidAuthException("Userid " + userId + " password is not Correct");
        }
        throw new InvalidAuthException("Userid " + userId + " not Found");
    }

    public Users getUsersResponseEntity(String authorizationHeader) throws InvalidAuthException {
        Optional<Users> byId = getUser(authorizationHeader);
        if (byId.isPresent()) {
            return byId.get();
        }
        throw new InvalidAuthException();
    }

    public Optional<Users> getUser(String authorizationHeader) {
        Tokens token = tokenRepo.findByToken(authorizationHeader);
        return userRepo.findById(token.getUserId());
    }
}
