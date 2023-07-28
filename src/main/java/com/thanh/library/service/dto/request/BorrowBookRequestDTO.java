package com.thanh.library.service.dto.request;

import java.util.Objects;

public class BorrowBookRequestDTO {

    private Long bookCopyId;
    private Long userId;

    public BorrowBookRequestDTO() {}

    public BorrowBookRequestDTO(Long bookCopyId, Long userId) {
        this.bookCopyId = bookCopyId;
        this.userId = userId;
    }

    public Long getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(Long bookCopyId) {
        this.bookCopyId = bookCopyId;
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
        BorrowBookRequestDTO that = (BorrowBookRequestDTO) o;
        return Objects.equals(bookCopyId, that.bookCopyId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookCopyId, userId);
    }

    @Override
    public String toString() {
        return "BorrowBookRequest{" + "bookCopyId=" + bookCopyId + ", userId=" + userId + '}';
    }
}
