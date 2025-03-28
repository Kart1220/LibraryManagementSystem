package last.project.lms;

import com.github.javafaker.Faker;
import last.project.lms.entities.Books;
import last.project.lms.entities.BooksLent;
import last.project.lms.entities.Users;
import last.project.lms.repo.BookRepo;
import last.project.lms.repo.LentRepo;
import last.project.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.System.exit;

@SpringBootApplication
public class GenerateData implements CommandLineRunner {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @Autowired
    private LentRepo lentRepo;

    public static void main(String[] args) {
        SpringApplication.run(GenerateData.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        userRepo.deleteAll();
        Users user = new Users("admin", "admin", "admin@lms.edu", "admin", "admin");
        userRepo.save(user);
        user = new Users("reader1", "reader1", "reader1@lms.edu", "reader1", "user");
        userRepo.save(user);
        user = new Users("reader2", "reader2", "reader2@lms.edu", "reader2", "user");
        userRepo.save(user);
        user = new Users("reader3", "reader3", "reader3@lms.edu", "reader3", "user");
        userRepo.save(user);
        System.out.println("User created successfully");

        bookRepo.deleteAll();
        Faker faker = new Faker();
        ArrayList<Books> books = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Books book = new Books();
            book.setName(faker.book().title());
            book.setAuthor(faker.book().author());
            book.setTotalBooks(faker.number().numberBetween(1, 100));
            book.setAvailableBooks(book.getTotalBooks());
            bookRepo.save(book);
            books.add(book);
        }

        lentRepo.deleteAll();
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            int userId = random.nextInt(3) + 1;
            Books book = books.get(random.nextInt(books.size()));
            BooksLent lend = new BooksLent();
            lend.setBookId(book.getBookId());
            lend.setUserId("reader" + userId);
            if (book.getAvailableBooks() > 0) {
                book.setAvailableBooks(book.getAvailableBooks() - 1);
                bookRepo.save(book);
                lentRepo.save(lend);
            }
        }

        exit(0);
    }
}
