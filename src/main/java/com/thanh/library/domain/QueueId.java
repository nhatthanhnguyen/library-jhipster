package com.thanh.library.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class QueueId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "book_id")
    private Long bookId;

    public QueueId(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public QueueId() {}

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
        QueueId queueId = (QueueId) o;
        return Objects.equals(userId, queueId.userId) && Objects.equals(bookId, queueId.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, bookId);
    }

    @Override
    public String toString() {
        return "QueueId{" + "userId=" + userId + ", bookId=" + bookId + '}';
    }
}
