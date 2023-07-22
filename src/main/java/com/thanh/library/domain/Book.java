package com.thanh.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "checkouts", "reservations", "notifications", "book" }, allowSetters = true)
    private Set<BookCopy> bookCopies = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "book" }, allowSetters = true)
    private Set<Queue> queues = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "rel_book__author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_book__category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Publisher publisher;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Book isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Set<BookCopy> getBookCopies() {
        return this.bookCopies;
    }

    public void setBookCopies(Set<BookCopy> bookCopies) {
        if (this.bookCopies != null) {
            this.bookCopies.forEach(i -> i.setBook(null));
        }
        if (bookCopies != null) {
            bookCopies.forEach(i -> i.setBook(this));
        }
        this.bookCopies = bookCopies;
    }

    public Book bookCopies(Set<BookCopy> bookCopies) {
        this.setBookCopies(bookCopies);
        return this;
    }

    public Book addBookCopy(BookCopy bookCopy) {
        this.bookCopies.add(bookCopy);
        bookCopy.setBook(this);
        return this;
    }

    public Book removeBookCopy(BookCopy bookCopy) {
        this.bookCopies.remove(bookCopy);
        bookCopy.setBook(null);
        return this;
    }

    public Set<Queue> getQueues() {
        return this.queues;
    }

    public void setQueues(Set<Queue> queues) {
        if (this.queues != null) {
            this.queues.forEach(i -> i.setBook(null));
        }
        if (queues != null) {
            queues.forEach(i -> i.setBook(this));
        }
        this.queues = queues;
    }

    public Book queues(Set<Queue> queues) {
        this.setQueues(queues);
        return this;
    }

    public Book addQueue(Queue queue) {
        this.queues.add(queue);
        queue.setBook(this);
        return this;
    }

    public Book removeQueue(Queue queue) {
        this.queues.remove(queue);
        queue.setBook(null);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Book categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Book addCategory(Category category) {
        this.categories.add(category);
        category.getBooks().add(this);
        return this;
    }

    public Book removeCategory(Category category) {
        this.categories.remove(category);
        category.getBooks().remove(this);
        return this;
    }

    public Publisher getPublisher() {
        return this.publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Book publisher(Publisher publisher) {
        this.setPublisher(publisher);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
