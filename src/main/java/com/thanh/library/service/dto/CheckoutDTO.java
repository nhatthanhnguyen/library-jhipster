package com.thanh.library.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.thanh.library.domain.Checkout} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CheckoutDTO implements Serializable {

    private UUID id;

    private Instant startTime;

    private Instant endTime;

    private Boolean isReturned;

    private UserDTO user;

    private BookCopyDTO bookCopy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsReturned() {
        return isReturned;
    }

    public void setIsReturned(Boolean isReturned) {
        this.isReturned = isReturned;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BookCopyDTO getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopyDTO bookCopy) {
        this.bookCopy = bookCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CheckoutDTO)) {
            return false;
        }

        CheckoutDTO checkoutDTO = (CheckoutDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, checkoutDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CheckoutDTO{" +
            "id='" + getId() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isReturned='" + getIsReturned() + "'" +
            ", user=" + getUser() +
            ", bookCopy=" + getBookCopy() +
            "}";
    }
}
