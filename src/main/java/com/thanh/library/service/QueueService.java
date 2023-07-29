package com.thanh.library.service;

import com.thanh.library.domain.Queue;
import com.thanh.library.repository.QueueRepository;
import com.thanh.library.service.dto.QueueDTO;
import com.thanh.library.service.mapper.QueueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Queue}.
 */
@Service
@Transactional
public class QueueService {

    private final Logger log = LoggerFactory.getLogger(QueueService.class);

    private final QueueRepository queueRepository;

    private final QueueMapper queueMapper;

    public QueueService(QueueRepository queueRepository, QueueMapper queueMapper) {
        this.queueRepository = queueRepository;
        this.queueMapper = queueMapper;
    }

    /**
     * Save a queue.
     *
     * @param queueDTO the entity to save.
     * @return the persisted entity.
     */
    public QueueDTO save(QueueDTO queueDTO) {
        log.debug("Request to save Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    /**
     * Update a queue.
     *
     * @param queueDTO the entity to save.
     * @return the persisted entity.
     */
    public QueueDTO update(QueueDTO queueDTO) {
        log.debug("Request to update Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    /**
     * Partially update a queue.
     *
     * @param queueDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QueueDTO> partialUpdate(QueueDTO queueDTO) {
        log.debug("Request to partially update Queue : {}", queueDTO);

        return queueRepository
            .findById(queueDTO.getId())
            .map(existingQueue -> {
                queueMapper.partialUpdate(existingQueue, queueDTO);

                return existingQueue;
            })
            .map(queueRepository::save)
            .map(queueMapper::toDto);
    }

    /**
     * Get all the queues.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<QueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Queues");
        return queueRepository.findAll(pageable).map(queueMapper::toDto);
    }

    /**
     * Get all the queues with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<QueueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return queueRepository.findAllWithEagerRelationships(pageable).map(queueMapper::toDto);
    }

    /**
     * Get one queue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<QueueDTO> findOne(Long id) {
        log.debug("Request to get Queue : {}", id);
        return queueRepository.findOneWithEagerRelationships(id).map(queueMapper::toDto);
    }

    /**
     * Delete the queue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Queue : {}", id);
        queueRepository.deleteById(id);
    }
}
