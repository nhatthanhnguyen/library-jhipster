package com.thanh.library.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.thanh.library.domain.Queue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QueueDTO implements Serializable {

    private Long id;

    private Instant createdAt;

    private UserDTO user;

    private BookDTO book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueDTO)) {
            return false;
        }

        QueueDTO queueDTO = (QueueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, queueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QueueDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", book=" + getBook() +
            "}";
    }
}
