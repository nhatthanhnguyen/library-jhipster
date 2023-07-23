package com.thanh.library.service;

import com.thanh.library.service.dto.CheckoutDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.thanh.library.domain.Checkout}.
 */
public interface CheckoutService {
    /**
     * Save a checkout.
     *
     * @param checkoutDTO the entity to save.
     * @return the persisted entity.
     */
    CheckoutDTO save(CheckoutDTO checkoutDTO);

    /**
     * Updates a checkout.
     *
     * @param checkoutDTO the entity to update.
     * @return the persisted entity.
     */
    CheckoutDTO update(CheckoutDTO checkoutDTO);

    /**
     * Partially updates a checkout.
     *
     * @param checkoutDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CheckoutDTO> partialUpdate(CheckoutDTO checkoutDTO);

    /**
     * Get all the checkouts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CheckoutDTO> findAll(Pageable pageable);

    /**
     * Get all the checkouts with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CheckoutDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" checkout.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CheckoutDTO> findOne(UUID id);

    /**
     * Delete the "id" checkout.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);
}
