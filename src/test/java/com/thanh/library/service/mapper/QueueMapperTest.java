package com.thanh.library.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueueMapperTest {

    private QueueMapper queueMapper;

    @BeforeEach
    public void setUp() {
        queueMapper = new QueueMapperImpl();
    }
}
