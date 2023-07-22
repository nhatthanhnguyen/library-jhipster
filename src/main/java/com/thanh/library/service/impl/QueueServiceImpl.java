package com.thanh.library.service.impl;

import com.thanh.library.domain.Queue;
import com.thanh.library.repository.QueueRepository;
import com.thanh.library.service.QueueService;
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
public class QueueServiceImpl implements QueueService {

    private final Logger log = LoggerFactory.getLogger(QueueServiceImpl.class);

    private final QueueRepository queueRepository;

    private final QueueMapper queueMapper;

    public QueueServiceImpl(QueueRepository queueRepository, QueueMapper queueMapper) {
        this.queueRepository = queueRepository;
        this.queueMapper = queueMapper;
    }

    @Override
    public QueueDTO save(QueueDTO queueDTO) {
        log.debug("Request to save Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    @Override
    public QueueDTO update(QueueDTO queueDTO) {
        log.debug("Request to update Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public Page<QueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Queues");
        return queueRepository.findAll(pageable).map(queueMapper::toDto);
    }

    public Page<QueueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return queueRepository.findAllWithEagerRelationships(pageable).map(queueMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QueueDTO> findOne(Long id) {
        log.debug("Request to get Queue : {}", id);
        return queueRepository.findOneWithEagerRelationships(id).map(queueMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Queue : {}", id);
        queueRepository.deleteById(id);
    }
}
