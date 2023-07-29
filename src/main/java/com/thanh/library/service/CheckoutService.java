package com.thanh.library.service;

import com.thanh.library.domain.Checkout;
import com.thanh.library.repository.CheckoutRepository;
import com.thanh.library.service.dto.CheckoutDTO;
import com.thanh.library.service.mapper.CheckoutMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Checkout}.
 */
@Service
@Transactional
public class CheckoutService {

    private final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CheckoutRepository checkoutRepository;

    private final CheckoutMapper checkoutMapper;

    public CheckoutService(CheckoutRepository checkoutRepository, CheckoutMapper checkoutMapper) {
        this.checkoutRepository = checkoutRepository;
        this.checkoutMapper = checkoutMapper;
    }

    /**
     * Save a checkout.
     *
     * @param checkoutDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckoutDTO save(CheckoutDTO checkoutDTO) {
        log.debug("Request to save Checkout : {}", checkoutDTO);
        Checkout checkout = checkoutMapper.toEntity(checkoutDTO);
        checkout = checkoutRepository.save(checkout);
        return checkoutMapper.toDto(checkout);
    }

    /**
     * Update a checkout.
     *
     * @param checkoutDTO the entity to save.
     * @return the persisted entity.
     */
    public CheckoutDTO update(CheckoutDTO checkoutDTO) {
        log.debug("Request to update Checkout : {}", checkoutDTO);
        Checkout checkout = checkoutMapper.toEntity(checkoutDTO);
        checkout = checkoutRepository.save(checkout);
        return checkoutMapper.toDto(checkout);
    }

    /**
     * Partially update a checkout.
     *
     * @param checkoutDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CheckoutDTO> partialUpdate(CheckoutDTO checkoutDTO) {
        log.debug("Request to partially update Checkout : {}", checkoutDTO);

        return checkoutRepository
            .findById(checkoutDTO.getId())
            .map(existingCheckout -> {
                checkoutMapper.partialUpdate(existingCheckout, checkoutDTO);

                return existingCheckout;
            })
            .map(checkoutRepository::save)
            .map(checkoutMapper::toDto);
    }

    /**
     * Get all the checkouts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CheckoutDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Checkouts");
        return checkoutRepository.findAll(pageable).map(checkoutMapper::toDto);
    }

    /**
     * Get all the checkouts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CheckoutDTO> findAllWithEagerRelationships(Pageable pageable) {
        return checkoutRepository.findAllWithEagerRelationships(pageable).map(checkoutMapper::toDto);
    }

    /**
     * Get one checkout by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CheckoutDTO> findOne(Long id) {
        log.debug("Request to get Checkout : {}", id);
        return checkoutRepository.findOneWithEagerRelationships(id).map(checkoutMapper::toDto);
    }

    /**
     * Delete the checkout by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Checkout : {}", id);
        checkoutRepository.deleteById(id);
    }
}
