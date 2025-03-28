package last.project.lms.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "lent")
public class BooksLent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long lentId;

    private String userId;
    private long bookId;


    public long getLentId() {
        return lentId;
    }

    public void setLentId(long lentId) {
        this.lentId = lentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
}
