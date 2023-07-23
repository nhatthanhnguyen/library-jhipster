package com.thanh.library.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookCopyMapperTest {

    private BookCopyMapper bookCopyMapper;

    @BeforeEach
    public void setUp() {
        bookCopyMapper = new BookCopyMapperImpl();
    }
}
