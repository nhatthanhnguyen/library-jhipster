package com.thanh.library.service.dto.request;

import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.dto.UserDTO;
import java.util.Objects;

public class BorrowBookRequestDTO {

    private UserDTO user;

    private BookCopyDTO bookCopy;

    public BorrowBookRequestDTO() {}

    public BorrowBookRequestDTO(UserDTO user, BookCopyDTO bookCopy) {
        this.user = user;
        this.bookCopy = bookCopy;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowBookRequestDTO that = (BorrowBookRequestDTO) o;
        return Objects.equals(user, that.user) && Objects.equals(bookCopy, that.bookCopy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, bookCopy);
    }

    @Override
    public String toString() {
        return "BorrowBookRequestDTO{" + "user=" + user + ", bookCopy=" + bookCopy + '}';
    }
}
