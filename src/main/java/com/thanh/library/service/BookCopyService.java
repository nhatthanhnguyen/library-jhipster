package com.thanh.library.service;

import com.thanh.library.domain.*;
import com.thanh.library.domain.enumeration.Type;
import com.thanh.library.repository.*;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.service.mapper.BookCopyMapper;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
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

    private final CheckoutRepository checkoutRepository;

    private final ReservationRepository reservationRepository;

    private final BookRepository bookRepository;

    private final QueueRepository queueRepository;

    private final NotificationRepository notificationRepository;

    private final BookCopyMapper bookCopyMapper;

    private final MailService mailService;

    public BookCopyService(
        BookCopyRepository bookCopyRepository,
        CheckoutRepository checkoutRepository,
        ReservationRepository reservationRepository,
        BookRepository bookRepository,
        QueueRepository queueRepository,
        NotificationRepository notificationRepository,
        BookCopyMapper bookCopyMapper,
        MailService mailService
    ) {
        this.bookCopyRepository = bookCopyRepository;
        this.checkoutRepository = checkoutRepository;
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.queueRepository = queueRepository;
        this.notificationRepository = notificationRepository;
        this.bookCopyMapper = bookCopyMapper;
        this.mailService = mailService;
    }

    public BookCopyDTO save(BookCopyDTO bookCopyDTO) {
        log.debug("Request to save BookCopy : {}", bookCopyDTO);
        Long bookId = bookCopyDTO.getBook().getId();
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "idnotfound"));
        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setIsDeleted(false);
        bookCopy = bookCopyRepository.save(bookCopy);
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

    @Transactional(readOnly = true)
    public Page<BookCopyDTO> getAllBookCopiesPaginationByBook(Long bookId, Pageable pageable) {
        bookRepository.findById(bookId).orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "idnotfound"));
        return bookCopyRepository.findBookCopiesByBookId(bookId, pageable).map(bookCopyMapper::toDto);
    }

    public List<BookCopyDTO> getAllAvailableBookCopies() {
        return bookCopyRepository
            .findAllBookCopiesThatIsNotDeleted(Sort.by("id"))
            .stream()
            .map(bookCopyMapper::toDto)
            .collect(Collectors.toList());
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
        BookCopy bookCopy = bookCopyRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Book Copy not found", "BookCopy", "idnotfound"));
        if (bookCopy.getIsDeleted()) {
            throw new BadRequestAlertException("Book Copy already deleted", "BookCopy", "bookcopyalreadydeleted");
        }
        Checkout checkout = checkoutRepository.findThatBookCopyIsBorrowed(bookCopy.getId()).orElse(null);
        if (checkout != null) {
            throw new BadRequestAlertException("Book Copy is borrowed", "BookCopy", "bookcopyisborrowed");
        }
        Reservation reservation = reservationRepository.findThatBookCopyIsHold(bookCopy.getId()).orElse(null);
        if (reservation != null) {
            throw new BadRequestAlertException("Book Copy is hold", "BookCopy", "bookcopyishold");
        }
        bookCopy.setIsDeleted(true);
        bookCopyRepository.save(bookCopy);
    }

    public void restore(Long id) {
        BookCopy bookCopy = bookCopyRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Book Copy not found", "BookCopy", "idnotfound"));
        if (!bookCopy.getIsDeleted()) {
            throw new BadRequestAlertException("Book Copy already restored", "BookCopy", "bookcopyalreadyrestored");
        }
        bookCopy.setIsDeleted(false);
        bookCopyRepository.save(bookCopy);
    }
}
