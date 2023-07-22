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
 * A BookCopy.
 */
@Entity
@Table(name = "book_copy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "year_published", nullable = false)
    private Integer yearPublished;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "bookCopy")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "bookCopy" }, allowSetters = true)
    private Set<Checkout> checkouts = new HashSet<>();

    @OneToMany(mappedBy = "bookCopy")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "bookCopy" }, allowSetters = true)
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "bookCopy")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "bookCopy" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "bookCopies", "queues", "authors", "categories", "publisher" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookCopy id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYearPublished() {
        return this.yearPublished;
    }

    public BookCopy yearPublished(Integer yearPublished) {
        this.setYearPublished(yearPublished);
        return this;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public BookCopy isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Set<Checkout> getCheckouts() {
        return this.checkouts;
    }

    public void setCheckouts(Set<Checkout> checkouts) {
        if (this.checkouts != null) {
            this.checkouts.forEach(i -> i.setBookCopy(null));
        }
        if (checkouts != null) {
            checkouts.forEach(i -> i.setBookCopy(this));
        }
        this.checkouts = checkouts;
    }

    public BookCopy checkouts(Set<Checkout> checkouts) {
        this.setCheckouts(checkouts);
        return this;
    }

    public BookCopy addCheckout(Checkout checkout) {
        this.checkouts.add(checkout);
        checkout.setBookCopy(this);
        return this;
    }

    public BookCopy removeCheckout(Checkout checkout) {
        this.checkouts.remove(checkout);
        checkout.setBookCopy(null);
        return this;
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(i -> i.setBookCopy(null));
        }
        if (reservations != null) {
            reservations.forEach(i -> i.setBookCopy(this));
        }
        this.reservations = reservations;
    }

    public BookCopy reservations(Set<Reservation> reservations) {
        this.setReservations(reservations);
        return this;
    }

    public BookCopy addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setBookCopy(this);
        return this;
    }

    public BookCopy removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setBookCopy(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setBookCopy(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setBookCopy(this));
        }
        this.notifications = notifications;
    }

    public BookCopy notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public BookCopy addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setBookCopy(this);
        return this;
    }

    public BookCopy removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setBookCopy(null);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookCopy book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopy)) {
            return false;
        }
        return id != null && id.equals(((BookCopy) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopy{" +
            "id=" + getId() +
            ", yearPublished=" + getYearPublished() +
            ", isDeleted='" + getIsDeleted() + "'" +
            "}";
    }
}
