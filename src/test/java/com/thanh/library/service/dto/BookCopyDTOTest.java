package com.thanh.library.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookCopyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookCopyDTO.class);
        BookCopyDTO bookCopyDTO1 = new BookCopyDTO();
        bookCopyDTO1.setId(UUID.randomUUID());
        BookCopyDTO bookCopyDTO2 = new BookCopyDTO();
        assertThat(bookCopyDTO1).isNotEqualTo(bookCopyDTO2);
        bookCopyDTO2.setId(bookCopyDTO1.getId());
        assertThat(bookCopyDTO1).isEqualTo(bookCopyDTO2);
        bookCopyDTO2.setId(UUID.randomUUID());
        assertThat(bookCopyDTO1).isNotEqualTo(bookCopyDTO2);
        bookCopyDTO1.setId(null);
        assertThat(bookCopyDTO1).isNotEqualTo(bookCopyDTO2);
    }
}
