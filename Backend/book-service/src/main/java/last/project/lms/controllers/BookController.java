package last.project.lms.controllers;

import last.project.lms.entities.Books;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookRepo bookRepo;
    @Autowired
    LentRepo lentRepo;

    @Autowired
    AuthService authService;

    @GetMapping("/{bookId}")
    public ResponseEntity getBook(@PathVariable("bookId") String bookId, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        return ResponseEntity.ok(bookRepo.findById(Long.valueOf(bookId)));
    }

    @GetMapping("")
    public ResponseEntity getAllBooks() throws InvalidAuthException {
        return ResponseEntity.ok(bookRepo.findAll());
    }

    @PostMapping("")
    public ResponseEntity addBook(@RequestBody Books book, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            if (user.get().getRole().equals("admin")) {
                bookRepo.save(book);
                bookRepo.flush();
                logger.info("Row saved to backend");
                return ResponseEntity.ok(book);
            }
            throw new InvalidAuthException("Invalid Auth");
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @PutMapping("")
    public ResponseEntity updateBook(@RequestBody Books book, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            if (user.get().getRole().equals("admin")) {
                Optional<Books> existingBook = bookRepo.findById(book.getBookId());
                if (existingBook.isPresent()) {
                    existingBook.get().setName(book.getName());
                    existingBook.get().setAuthor(book.getAuthor());
                    existingBook.get().setTotalBooks(book.getTotalBooks());
                    existingBook.get().setAvailableBooks(book.getAvailableBooks());
                    bookRepo.save(existingBook.get());
                    return ResponseEntity.ok(existingBook.get());
                }
                return ResponseEntity.notFound().build();
            }
            throw new InvalidAuthException("Invalid Auth");
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity deleteBook(@PathVariable("bookId") String bookId, @RequestHeader("Authorization") @DefaultValue("XXX") String authorizationHeader) throws InvalidAuthException {
        Optional<Users> user = authService.getUser(authorizationHeader);
        if (user.isPresent()) {
            if (user.get().getRole().equals("admin")) {
                lentRepo.deleteByBookId(Long.parseLong(bookId));
                bookRepo.deleteById(Long.valueOf(bookId));
                bookRepo.flush();
                return ResponseEntity.ok(bookId);
            }
            throw new InvalidAuthException("Invalid Auth");
        }
        throw new InvalidAuthException("Invalid Auth");
    }

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<Object> handleInvalidAuthException(InvalidAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
