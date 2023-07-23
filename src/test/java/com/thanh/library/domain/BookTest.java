package com.thanh.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Book.class);
        Book book1 = new Book();
        book1.setId(UUID.randomUUID());
        Book book2 = new Book();
        book2.setId(book1.getId());
        assertThat(book1).isEqualTo(book2);
        book2.setId(UUID.randomUUID());
        assertThat(book1).isNotEqualTo(book2);
        book1.setId(null);
        assertThat(book1).isNotEqualTo(book2);
    }
}
