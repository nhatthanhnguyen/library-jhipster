package com.thanh.library.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.library.domain.QueueId;
import com.thanh.library.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QueueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(QueueDTO.class);
        QueueDTO queueDTO1 = new QueueDTO();
        queueDTO1.setId(new QueueId(1L, 1L));
        QueueDTO queueDTO2 = new QueueDTO();
        assertThat(queueDTO1).isNotEqualTo(queueDTO2);
        queueDTO2.setId(queueDTO1.getId());
        assertThat(queueDTO1).isEqualTo(queueDTO2);
        queueDTO2.setId(new QueueId(2L, 2L));
        assertThat(queueDTO1).isNotEqualTo(queueDTO2);
        queueDTO1.setId(null);
        assertThat(queueDTO1).isNotEqualTo(queueDTO2);
    }
}
