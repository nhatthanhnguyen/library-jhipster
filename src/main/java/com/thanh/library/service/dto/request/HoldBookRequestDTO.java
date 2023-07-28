package com.thanh.library.service.dto.request;

import java.util.Objects;

public class HoldBookRequestDTO {

    private Long bookId;
    private Long userId;

    public HoldBookRequestDTO() {}

    public HoldBookRequestDTO(Long bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoldBookRequestDTO that = (HoldBookRequestDTO) o;
        return Objects.equals(bookId, that.bookId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, userId);
    }

    @Override
    public String toString() {
        return "HoldBookRequestDTO{" + "bookId=" + bookId + ", userId=" + userId + '}';
    }
}
