package com.thanh.library.service.dto.request;

import java.util.Objects;

public class BorrowBookRequestDTO {

    private Long userId;

    private Long bookCopyId;

    public BorrowBookRequestDTO() {}

    public BorrowBookRequestDTO(Long userId, Long bookCopyId) {
        this.userId = userId;
        this.bookCopyId = bookCopyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(Long bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowBookRequestDTO that = (BorrowBookRequestDTO) o;
        return Objects.equals(userId, that.userId) && Objects.equals(bookCopyId, that.bookCopyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookCopyId);
    }
}
