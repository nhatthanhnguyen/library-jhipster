package com.thanh.library.service;

import com.thanh.library.domain.*;
import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.repository.BookRepository;
import com.thanh.library.repository.QueueRepository;
import com.thanh.library.repository.UserRepository;
import com.thanh.library.service.dto.QueueDTO;
import com.thanh.library.service.dto.request.BorrowBookRequestDTO;
import com.thanh.library.service.dto.response.ResponseMessageDTO;
import com.thanh.library.service.mapper.QueueMapper;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.util.List;
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

    public QueueDTO save(QueueDTO queueDTO) {
        log.debug("Request to save Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

    public QueueDTO update(QueueDTO queueDTO) {
        log.debug("Request to update Queue : {}", queueDTO);
        Queue queue = queueMapper.toEntity(queueDTO);
        queue = queueRepository.save(queue);
        return queueMapper.toDto(queue);
    }

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

    @Transactional(readOnly = true)
    public Page<QueueDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Queues");
        return queueRepository.findAll(pageable).map(queueMapper::toDto);
    }

    public Page<QueueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return queueRepository.findAllWithEagerRelationships(pageable).map(queueMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<QueueDTO> findOne(Long id) {
        log.debug("Request to get Queue : {}", id);
        return queueRepository.findOneWithEagerRelationships(id).map(queueMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete Queue : {}", id);
        queueRepository.deleteById(id);
    }
}
