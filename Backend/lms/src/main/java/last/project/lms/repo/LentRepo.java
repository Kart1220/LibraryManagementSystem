package last.project.lms.repo;

import jakarta.transaction.Transactional;
import last.project.lms.entities.BooksLent;
import last.project.lms.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LentRepo extends JpaRepository<BooksLent, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM BooksLent l WHERE l.bookId = :bookId")
    void deleteByBookId(long bookId);

    List<BooksLent> findAllByUserId(String userId);

}
