package com.thanh.library.service;

import com.thanh.library.service.dto.PublisherDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.thanh.library.domain.Publisher}.
 */
public interface PublisherService {
    /**
     * Save a publisher.
     *
     * @param publisherDTO the entity to save.
     * @return the persisted entity.
     */
    PublisherDTO save(PublisherDTO publisherDTO);

    /**
     * Updates a publisher.
     *
     * @param publisherDTO the entity to update.
     * @return the persisted entity.
     */
    PublisherDTO update(PublisherDTO publisherDTO);

    /**
     * Partially updates a publisher.
     *
     * @param publisherDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PublisherDTO> partialUpdate(PublisherDTO publisherDTO);

    /**
     * Get all the publishers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PublisherDTO> findAll(Pageable pageable);

    /**
     * Get the "id" publisher.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PublisherDTO> findOne(Long id);

    /**
     * Delete the "id" publisher.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
