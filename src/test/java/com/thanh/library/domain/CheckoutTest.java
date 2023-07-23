package com.thanh.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CheckoutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Checkout.class);
        Checkout checkout1 = new Checkout();
        checkout1.setId(UUID.randomUUID());
        Checkout checkout2 = new Checkout();
        checkout2.setId(checkout1.getId());
        assertThat(checkout1).isEqualTo(checkout2);
        checkout2.setId(UUID.randomUUID());
        assertThat(checkout1).isNotEqualTo(checkout2);
        checkout1.setId(null);
        assertThat(checkout1).isNotEqualTo(checkout2);
    }
}
