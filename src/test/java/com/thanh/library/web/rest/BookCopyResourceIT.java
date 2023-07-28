package com.thanh.library.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.thanh.library.IntegrationTest;
import com.thanh.library.domain.BookCopy;
import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.service.BookCopyService;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.mapper.BookCopyMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookCopyResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookCopyResourceIT {

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String ENTITY_API_URL = "/api/book-copies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookCopyRepository bookCopyRepositoryMock;

    @Autowired
    private BookCopyMapper bookCopyMapper;

    @Mock
    private BookCopyService bookCopyServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookCopyMockMvc;

    private BookCopy bookCopy;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().isDeleted(DEFAULT_IS_DELETED);
        return bookCopy;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookCopy createUpdatedEntity(EntityManager em) {
        BookCopy bookCopy = new BookCopy().isDeleted(UPDATED_IS_DELETED);
        return bookCopy;
    }

    @BeforeEach
    public void initTest() {
        bookCopy = createEntity(em);
    }

    @Test
    @Transactional
    void createBookCopy() throws Exception {
        int databaseSizeBeforeCreate = bookCopyRepository.findAll().size();
        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopyDTO)))
            .andExpect(status().isCreated());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeCreate + 1);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
    }

    @Test
    @Transactional
    void createBookCopyWithExistingId() throws Exception {
        // Create the BookCopy with an existing ID
        bookCopy.setId(1L);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        int databaseSizeBeforeCreate = bookCopyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookCopyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookCopies() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get all the bookCopyList
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookCopy.getId().intValue())))
            .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookCopiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookCopyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookCopyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookCopyServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBookCopiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookCopyServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookCopyMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bookCopyRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        // Get the bookCopy
        restBookCopyMockMvc
            .perform(get(ENTITY_API_URL_ID, bookCopy.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookCopy.getId().intValue()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingBookCopy() throws Exception {
        // Get the bookCopy
        restBookCopyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy
        BookCopy updatedBookCopy = bookCopyRepository.findById(bookCopy.getId()).get();
        // Disconnect from session so that the updates on updatedBookCopy are not directly saved in db
        em.detach(updatedBookCopy);
        updatedBookCopy.isDeleted(UPDATED_IS_DELETED);
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(updatedBookCopy);

        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void putNonExistingBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bookCopyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
    }

    @Test
    @Transactional
    void fullUpdateBookCopyWithPatch() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();

        // Update the bookCopy using partial update
        BookCopy partialUpdatedBookCopy = new BookCopy();
        partialUpdatedBookCopy.setId(bookCopy.getId());

        partialUpdatedBookCopy.isDeleted(UPDATED_IS_DELETED);

        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookCopy.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBookCopy))
            )
            .andExpect(status().isOk());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
        BookCopy testBookCopy = bookCopyList.get(bookCopyList.size() - 1);
        assertThat(testBookCopy.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    void patchNonExistingBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookCopyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookCopy() throws Exception {
        int databaseSizeBeforeUpdate = bookCopyRepository.findAll().size();
        bookCopy.setId(count.incrementAndGet());

        // Create the BookCopy
        BookCopyDTO bookCopyDTO = bookCopyMapper.toDto(bookCopy);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookCopyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bookCopyDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookCopy in the database
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookCopy() throws Exception {
        // Initialize the database
        bookCopyRepository.saveAndFlush(bookCopy);

        int databaseSizeBeforeDelete = bookCopyRepository.findAll().size();

        // Delete the bookCopy
        restBookCopyMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookCopy.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BookCopy> bookCopyList = bookCopyRepository.findAll();
        assertThat(bookCopyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
