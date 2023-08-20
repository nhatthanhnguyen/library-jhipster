package com.thanh.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QueueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Queue.class);
        Queue queue1 = new Queue();
        Queue queue2 = new Queue();
        queue2.setId(queue1.getId());
        assertThat(queue1).isEqualTo(queue2);
        assertThat(queue1).isNotEqualTo(queue2);
        queue1.setId(null);
        assertThat(queue1).isNotEqualTo(queue2);
    }
}
