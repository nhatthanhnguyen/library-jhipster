package com.thanh.library.service;

import com.thanh.library.domain.Book;
import com.thanh.library.domain.BookCopy;
import com.thanh.library.domain.Reservation;
import com.thanh.library.domain.User;
import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.repository.BookRepository;
import com.thanh.library.repository.ReservationRepository;
import com.thanh.library.repository.UserRepository;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.dto.BookDTO;
import com.thanh.library.service.dto.request.HoldBookRequestDTO;
import com.thanh.library.service.dto.response.ResponseMessageDTO;
import com.thanh.library.service.mapper.BookMapper;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Book}.
 */
@Service
@Transactional
public class BookService {

    private final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    private final BookCopyRepository bookCopyRepository;

    private final UserRepository userRepository;

    private final ReservationRepository reservationRepository;

    private final BookMapper bookMapper;

    public BookService(
        BookRepository bookRepository,
        BookCopyRepository bookCopyRepository,
        UserRepository userRepository,
        ReservationRepository reservationRepository,
        BookMapper bookMapper
    ) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.bookMapper = bookMapper;
    }

    public BookDTO save(BookDTO bookDTO) {
        log.debug("Request to save Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    public BookDTO update(BookDTO bookDTO) {
        log.debug("Request to update Book : {}", bookDTO);
        Book book = bookMapper.toEntity(bookDTO);
        book = bookRepository.save(book);
        return bookMapper.toDto(book);
    }

    public Optional<BookDTO> partialUpdate(BookDTO bookDTO) {
        log.debug("Request to partially update Book : {}", bookDTO);

        return bookRepository
            .findById(bookDTO.getId())
            .map(existingBook -> {
                bookMapper.partialUpdate(existingBook, bookDTO);

                return existingBook;
            })
            .map(bookRepository::save)
            .map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(pageable).map(bookMapper::toDto);
    }

    public Page<BookDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bookRepository.findAllWithEagerRelationships(pageable).map(bookMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<BookDTO> findOne(Long id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findOneWithEagerRelationships(id).map(bookMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }

    public ResponseMessageDTO holdBookByCurrentUser(Long bookId) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User is not login", "User", "usernotlogin"));

        User user = userRepository
            .findOneByLogin(userLogin)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }

        Book book = bookRepository
            .findById(bookId)
            .orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "booknotfound"));
        if (book.getIsDeleted()) {
            throw new BadRequestAlertException("Book is deleted", "Book", "bookisdeleted");
        }

        List<BookCopy> bookCopies = bookCopyRepository.findBookCopiesAvailableByBookId(bookId);
        if (bookCopies.isEmpty()) {
            throw new BadRequestAlertException("Book Copy is not available", "BookCopy", "bookcopynotavailable");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBookCopy(bookCopies.get(0));
        reservation.setStartTime(Instant.now());
        reservationRepository.save(reservation);

        return new ResponseMessageDTO(200, "Hold book successfully", Instant.now());
    }

    public ResponseMessageDTO holdBook(HoldBookRequestDTO holdBookRequestDTO) {
        User user = userRepository
            .findById(holdBookRequestDTO.getUserId())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "idnotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }

        Book book = bookRepository
            .findById(holdBookRequestDTO.getBookId())
            .orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "idnotfound"));
        if (book.getIsDeleted()) {
            throw new BadRequestAlertException("Book is deleted", "Book", "bookisdeleted");
        }

        List<BookCopy> bookCopies = bookCopyRepository.findBookCopiesAvailableByBookId(book.getId());
        if (bookCopies.isEmpty()) {
            throw new BadRequestAlertException("BookCopy is not available", "BookCopy", "bookcopynotavailable");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBookCopy(bookCopies.get(0));
        reservation.setStartTime(Instant.now());
        reservationRepository.save(reservation);

        return new ResponseMessageDTO(200, "Hold book successfully", Instant.now());
    }
}
