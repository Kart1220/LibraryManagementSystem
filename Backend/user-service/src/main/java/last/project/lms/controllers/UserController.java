package last.project.lms.controllers;

import last.project.lms.exceptions.InvalidAuthException;
import last.project.lms.service.AuthService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    AuthService authService;

    @GetMapping("")
    public ResponseEntity getUser(@RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        return ResponseEntity.ok(authService.getUsersResponseEntity(authorizationHeader));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody String userDataString) throws InvalidAuthException {
        logger.info("User trying to login");
        try {
            JSONObject userDetails = new JSONObject(userDataString);
            return ResponseEntity.ok(authService.login(userDetails.getString("username"), userDetails.getString("password")));
        } catch (Exception e) {
            logger.error("login Failed, {}", e.getMessage(), e);
            throw e;
        }
    }
    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<Object> handleInvalidAuthException(InvalidAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
