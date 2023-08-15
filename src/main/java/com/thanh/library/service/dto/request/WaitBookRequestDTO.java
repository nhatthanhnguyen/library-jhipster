package com.thanh.library.service.dto.request;

import java.util.Objects;

public class WaitBookRequestDTO {

    private Long userId;
    private Long bookId;

    public WaitBookRequestDTO() {}

    public WaitBookRequestDTO(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitBookRequestDTO that = (WaitBookRequestDTO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }

    @Override
    public String toString() {
        return "WaitBookRequestDTO{" + "userId=" + userId + ", bookId=" + bookId + '}';
    }
}
