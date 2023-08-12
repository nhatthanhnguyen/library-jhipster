package com.thanh.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Queue.
 */
@Entity
@Table(name = "queue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Queue implements Serializable {

    private static final long serialVersionUID = 1L;

    //    @Id
    //    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    //    @SequenceGenerator(name = "sequenceGenerator")
    //    @Column(name = "id")
    //    private Long id;
    @EmbeddedId
    private QueueId id;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("bookId")
    @JsonIgnoreProperties(value = { "publisher", "authors", "categories" }, allowSetters = true)
    @JoinColumn(name = "book_id")
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public QueueId getId() {
        return this.id;
    }

    public Queue id(QueueId id) {
        this.setId(id);
        return this;
    }

    public void setId(QueueId id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Queue createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Queue user(User user) {
        this.setUser(user);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Queue book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Queue)) {
            return false;
        }
        return id != null && id.equals(((Queue) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Queue{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
