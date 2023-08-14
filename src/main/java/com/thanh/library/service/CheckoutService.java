package com.thanh.library.service;

import com.thanh.library.domain.*;
import com.thanh.library.domain.enumeration.Type;
import com.thanh.library.repository.*;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.dto.CheckoutDTO;
import com.thanh.library.service.dto.request.BorrowBookRequestDTO;
import com.thanh.library.service.dto.request.ReturnBookRequestDTO;
import com.thanh.library.service.mapper.CheckoutMapper;
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
 * Service Implementation for managing {@link Checkout}.
 */
@Service
@Transactional
public class CheckoutService {

    private final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CheckoutRepository checkoutRepository;

    private final UserRepository userRepository;

    private final BookCopyRepository bookCopyRepository;

    private final ReservationRepository reservationRepository;

    private final QueueRepository queueRepository;

    private final NotificationRepository notificationRepository;

    private final CheckoutMapper checkoutMapper;

    private final BookRepository bookRepository;

    private final MailService mailService;

    public CheckoutService(
        CheckoutRepository checkoutRepository,
        UserRepository userRepository,
        BookCopyRepository bookCopyRepository,
        ReservationRepository reservationRepository,
        QueueRepository queueRepository,
        NotificationRepository notificationRepository,
        CheckoutMapper checkoutMapper,
        BookRepository bookRepository,
        MailService mailService
    ) {
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.notificationRepository = notificationRepository;
        this.checkoutMapper = checkoutMapper;
        this.bookRepository = bookRepository;
        this.mailService = mailService;
    }

    public CheckoutDTO save(CheckoutDTO checkoutDTO) {
        log.debug("Request to save Checkout : {}", checkoutDTO);
        Checkout checkout = checkoutMapper.toEntity(checkoutDTO);
        checkout = checkoutRepository.save(checkout);
        return checkoutMapper.toDto(checkout);
    }

    public CheckoutDTO update(CheckoutDTO checkoutDTO) {
        log.debug("Request to update Checkout : {}", checkoutDTO);
        Checkout checkout = checkoutMapper.toEntity(checkoutDTO);
        checkout = checkoutRepository.save(checkout);
        return checkoutMapper.toDto(checkout);
    }

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

    @Transactional(readOnly = true)
    public Page<CheckoutDTO> findAll(Long userId, Long bookCopyId, String status, Pageable pageable) {
        log.debug("Request to get all Checkouts");
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Login not found", "User", "loginnotfound"));
        User currentUser = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        Boolean isReturned = false;
        if (status.equalsIgnoreCase("returnsuccess")) {
            isReturned = true;
        }
        Authority roleLibrarian = new Authority();
        roleLibrarian.setName("ROLE_LIBRARIAN");
        // if current user is librarian
        if (currentUser.getAuthorities().contains(roleLibrarian)) {
            if (status.equalsIgnoreCase("borrowing")) {
                return checkoutRepository.findAllWithEndTimeNull(userId, bookCopyId, isReturned, pageable).map(checkoutMapper::toDto);
            }
            if (status.equalsIgnoreCase("all")) {
                return checkoutRepository.findAllWithCondition(userId, bookCopyId, pageable).map(checkoutMapper::toDto);
            }
            return checkoutRepository.findAllWithEndTimeNotNull(userId, bookCopyId, isReturned, pageable).map(checkoutMapper::toDto);
        }
        // if current user is reader
        if (status.equalsIgnoreCase("borrowing")) {
            return checkoutRepository
                .findAllWithEndTimeNull(currentUser.getId(), bookCopyId, isReturned, pageable)
                .map(checkoutMapper::toDto);
        }
        if (status.equalsIgnoreCase("all")) {
            return checkoutRepository.findAllWithCondition(currentUser.getId(), bookCopyId, pageable).map(checkoutMapper::toDto);
        }
        return checkoutRepository
            .findAllWithEndTimeNotNull(currentUser.getId(), bookCopyId, isReturned, pageable)
            .map(checkoutMapper::toDto);
    }

    public Page<CheckoutDTO> findAllWithEagerRelationships(Pageable pageable) {
        return checkoutRepository.findAllWithEagerRelationships(pageable).map(checkoutMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<CheckoutDTO> findOne(Long id) {
        log.debug("Request to get Checkout : {}", id);
        return checkoutRepository.findOneWithEagerRelationships(id).map(checkoutMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete Checkout : {}", id);
        Checkout checkout = checkoutRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Checkout not found", "Checkout", "idnotfound"));
        checkoutRepository.deleteById(id);
        Book book = checkout.getBookCopy().getBook();
        List<Queue> queues = queueRepository.findByBookId(book.getId());
        queues.forEach(queue -> {
            queueRepository.deleteById(queue.getId());
            Notification notification = new Notification();
            notification.setUser(checkout.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
    }

    public void borrowBook(BorrowBookRequestDTO borrowBookRequestDTO) {
        User user = userRepository
            .findById(borrowBookRequestDTO.getUser().getId())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "idnotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }
        BookCopy bookCopy = bookCopyRepository
            .findById(borrowBookRequestDTO.getBookCopy().getId())
            .orElseThrow(() -> new BadRequestAlertException("Book Copy not found", "BookCopy", "idnotfound"));
        if (bookCopy.getIsDeleted()) {
            throw new BadRequestAlertException("Book Copy is deleted", "BookCopy", "bookcopyisdeleted");
        }

        List<Checkout> checkouts = checkoutRepository.findAllThatBookCopyIsBorrowed(bookCopy.getId());
        List<Reservation> reservations = reservationRepository.findAllThatBookCopyIsBorrowed(bookCopy.getId());
        if (!checkouts.isEmpty() || !reservations.isEmpty()) {
            throw new BadRequestAlertException("Book Copy is not available", "BookCopy", "bookcopynotavailable");
        }
        Checkout checkout = new Checkout();
        checkout.setUser(user);
        checkout.setBookCopy(bookCopy);
        checkout.setStartTime(Instant.now());
        checkout.setIsReturned(false);
        checkoutRepository.save(checkout);
    }

    public void returnBook(ReturnBookRequestDTO returnBookRequestDTO) {
        Checkout checkout = checkoutRepository
            .findById(returnBookRequestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Checkout not found", "Checkout", "checkoutnotfound"));
        if (checkout.getIsReturned()) {
            throw new BadRequestAlertException("Book Copy is returned", "BookCopy", "bookcopyisreturned");
        }
        checkout.setIsReturned(returnBookRequestDTO.isSuccess());
        checkout.setEndTime(Instant.now());
        checkoutRepository.save(checkout);
        if (!returnBookRequestDTO.isSuccess()) {
            BookCopy bookCopy = checkout.getBookCopy();
            bookCopy.setIsDeleted(true);
            bookCopyRepository.save(bookCopy);
        }
        Book book = checkout.getBookCopy().getBook();
        List<Queue> queues = queueRepository.findByBookId(book.getId());
        queues.forEach(queue -> {
            queueRepository.deleteById(queue.getId());
            Notification notification = new Notification();
            notification.setUser(checkout.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
    }

    public void addToQueue(Long id) {
        String userLogin = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("User is not login", "User", "usernotlogin"));

        User user = userRepository
            .findOneByLogin(userLogin)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }

        Book book = bookRepository.findById(id).orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "booknotfound"));
        if (book.getIsDeleted()) {
            throw new BadRequestAlertException("Book is deleted", "Book", "bookisdeleted");
        }
        QueueId queueId = new QueueId();
        queueId.setBookId(book.getId());
        queueId.setUserId(user.getId());
        if (queueRepository.existsById(queueId)) {
            throw new BadRequestAlertException("Currently in queue", "Queue", "alreadyinqueue");
        }
        Queue queue = new Queue();
        queue.setId(queueId);
        queue.setBook(book);
        queue.setUser(user);
        queue.setCreatedAt(Instant.now());
        queueRepository.save(queue);
    }
}
