package com.thanh.library.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.thanh.library.domain.BookCopy} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopyDTO implements Serializable {

    private UUID id;

    @NotNull
    private Integer yearPublished;

    private Boolean isDeleted;

    private BookDTO book;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
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
        if (!(o instanceof BookCopyDTO)) {
            return false;
        }

        BookCopyDTO bookCopyDTO = (BookCopyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookCopyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopyDTO{" +
            "id='" + getId() + "'" +
            ", yearPublished=" + getYearPublished() +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", book=" + getBook() +
            "}";
    }
}
