package com.thanh.library.service;

import com.thanh.library.domain.Book;
import com.thanh.library.domain.BookCopy;
import com.thanh.library.domain.Notification;
import com.thanh.library.domain.Queue;
import com.thanh.library.domain.enumeration.Type;
import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.repository.NotificationRepository;
import com.thanh.library.repository.QueueRepository;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.mapper.BookCopyMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BookCopy}.
 */
@Service
@Transactional
public class BookCopyService {

    private final Logger log = LoggerFactory.getLogger(BookCopyService.class);

    private final BookCopyRepository bookCopyRepository;

    private final QueueRepository queueRepository;

    private final NotificationRepository notificationRepository;

    private final BookCopyMapper bookCopyMapper;

    private final MailService mailService;

    public BookCopyService(
        BookCopyRepository bookCopyRepository,
        QueueRepository queueRepository,
        NotificationRepository notificationRepository,
        BookCopyMapper bookCopyMapper,
        MailService mailService
    ) {
        this.bookCopyRepository = bookCopyRepository;
        this.queueRepository = queueRepository;
        this.notificationRepository = notificationRepository;
        this.bookCopyMapper = bookCopyMapper;
        this.mailService = mailService;
    }

    public BookCopyDTO save(BookCopyDTO bookCopyDTO) {
        log.debug("Request to save BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        Book book = bookCopy.getBook();
        List<Queue> queues = queueRepository.findByBookId(book.getId());
        BookCopy finalBookCopy = bookCopy;
        queues.forEach(queue -> {
            queueRepository.deleteById(queue.getId());
            Notification notification = new Notification();
            notification.setUser(queue.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notification.setBookCopy(finalBookCopy);
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
        return bookCopyMapper.toDto(bookCopy);
    }

    public BookCopyDTO update(BookCopyDTO bookCopyDTO) {
        log.debug("Request to update BookCopy : {}", bookCopyDTO);
        BookCopy bookCopy = bookCopyMapper.toEntity(bookCopyDTO);
        bookCopy = bookCopyRepository.save(bookCopy);
        return bookCopyMapper.toDto(bookCopy);
    }

    public Optional<BookCopyDTO> partialUpdate(BookCopyDTO bookCopyDTO) {
        log.debug("Request to partially update BookCopy : {}", bookCopyDTO);

        return bookCopyRepository
            .findById(bookCopyDTO.getId())
            .map(existingBookCopy -> {
                bookCopyMapper.partialUpdate(existingBookCopy, bookCopyDTO);

                return existingBookCopy;
            })
            .map(bookCopyRepository::save)
            .map(bookCopyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookCopyDTO> getAllBookCopiesPagination(Pageable pageable) {
        log.debug("Request to get all BookCopies");
        return bookCopyRepository.findAll(pageable).map(bookCopyMapper::toDto);
    }

    public List<BookCopyDTO> getAllBookCopies() {
        return bookCopyRepository.findAll(Sort.by("id")).stream().map(bookCopyMapper::toDto).collect(Collectors.toList());
    }

    public Page<BookCopyDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookCopyRepository.findAllWithEagerRelationships(pageable).map(bookCopyMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<BookCopyDTO> findOne(Long id) {
        log.debug("Request to get BookCopy : {}", id);
        return bookCopyRepository.findOneWithEagerRelationships(id).map(bookCopyMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete BookCopy : {}", id);
        bookCopyRepository.deleteById(id);
    }
}
