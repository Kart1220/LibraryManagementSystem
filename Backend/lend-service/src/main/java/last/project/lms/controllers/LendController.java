package last.project.lms.controllers;

import last.project.lms.entities.Books;
import last.project.lms.entities.BooksLent;
import last.project.lms.entities.Users;
import last.project.lms.exceptions.InvalidAuthException;
import last.project.lms.repo.BookRepo;
import last.project.lms.repo.LentRepo;
import last.project.lms.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/lend")
public class LendController {
    private static final Logger logger = LoggerFactory.getLogger(LendController.class);


    @Autowired
    LentRepo lentRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    AuthService authService;

    @GetMapping("/{lendId}")
    public ResponseEntity getLend(@PathVariable("lendId") String lendId, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        return ResponseEntity.ok(lentRepo.findById(Long.valueOf(lendId)));
    }

    @GetMapping("")
    public ResponseEntity getAllLends(@RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if(user.isPresent()) {
            if (user.get().getRole().equals("admin")) {
                return ResponseEntity.ok(lentRepo.findAll());
            }
            return ResponseEntity.ok(lentRepo.findAllByUserId(user.get().getUserId()));
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @PostMapping("")
    public ResponseEntity addLend(@RequestBody BooksLent lend, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            if (user.get().getRole().equals("user")) {
                Optional<Books> byId = bookRepo.findById(lend.getBookId());
                if (byId.isPresent()) {
                    Books book = byId.get();
                    if (book.getAvailableBooks() > 0) {
                        book.setAvailableBooks(book.getAvailableBooks() - 1);
                        bookRepo.save(book);
                        bookRepo.flush();
                        lentRepo.save(lend);
                        lentRepo.flush();
                        return ResponseEntity.ok(lend);
                    }
                    throw new RuntimeException("Not enough books for bookId: " + lend.getBookId());
                }
                throw new RuntimeException("Invalid Book ID: " + lend.getBookId());
            }
            throw new InvalidAuthException("only users and take books");
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @PutMapping("")
    public ResponseEntity updateLend(@RequestBody BooksLent lend, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            if (user.get().getRole().equals("user")) {
                Optional<Books> byId = bookRepo.findById(lend.getBookId());
                if (byId.isPresent()) {
                    Books book = byId.get();
                    if (book.getAvailableBooks() > 0) {
                        book.setAvailableBooks(book.getAvailableBooks() - 1);
                        bookRepo.save(book);
                        bookRepo.flush();
                        lentRepo.save(lend);
                        lentRepo.flush();
                        return ResponseEntity.ok(lend);
                    }
                    throw new RuntimeException("Not enough books for bookId: " + lend.getBookId());
                }
                throw new RuntimeException("Invalid Book ID: " + lend.getBookId());
            }
            throw new InvalidAuthException("only users and take books");
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @DeleteMapping("/{lendId}")
    public ResponseEntity deleteLend(@PathVariable("lendId") String lendId, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            Optional<BooksLent> lendOp = lentRepo.findById(Long.valueOf(lendId));
            if (lendOp.isPresent()) {
                BooksLent lend = lendOp.get();
                if ((user.get().getRole().equals("user") && lend.getUserId().equals(user.get().getUserId())) || user.get().getRole().equals("admin")) {
                    Optional<Books> byId = bookRepo.findById(lend.getBookId());
                    if (byId.isPresent()) {
                        Books book = byId.get();
                        book.setAvailableBooks(book.getAvailableBooks() + 1);
                        bookRepo.save(book);
                        bookRepo.flush();
                        lentRepo.deleteById(Long.valueOf(lendId));
                        lentRepo.flush();
                        return ResponseEntity.ok(lend);
                    }
                    throw new RuntimeException("Invalid Book ID: " + lend.getBookId());
                }
                throw new InvalidAuthException("only owners can delete");
            }
            throw new RuntimeException("Invalid Lend ID: " + lendId);
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<Object> handleInvalidAuthException(InvalidAuthException ex) {
        logger.error("failed because: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        logger.error("failed because: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
