package br.com.rponte.rinhadev.samples.books;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.ISBN;

import java.util.Objects;

import static org.hibernate.validator.constraints.ISBN.Type.ISBN_13;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_isbn", columnNames = "isbn")
    }
)
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @ISBN(type = ISBN_13)
    @Column(nullable = false, length = 13)
    private String isbn;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank
    @Size(max = 4000)
    @Column(nullable = false, length = 4000)
    private String description;

    @Version
    private int version;

    /**
     * @deprecated Exclusive use of Hibernate
     */
    @Deprecated
    public Book() {}

    public Book(@NotBlank @ISBN String isbn,
                @NotBlank @Size(max = 120) String title,
                @NotBlank @Size(max = 4000) String description) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
