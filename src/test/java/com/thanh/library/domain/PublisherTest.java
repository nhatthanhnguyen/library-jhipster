package com.thanh.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PublisherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Publisher.class);
        Publisher publisher1 = new Publisher();
        publisher1.setId(UUID.randomUUID());
        Publisher publisher2 = new Publisher();
        publisher2.setId(publisher1.getId());
        assertThat(publisher1).isEqualTo(publisher2);
        publisher2.setId(UUID.randomUUID());
        assertThat(publisher1).isNotEqualTo(publisher2);
        publisher1.setId(null);
        assertThat(publisher1).isNotEqualTo(publisher2);
    }
}
