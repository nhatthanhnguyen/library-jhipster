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
    public Page<CheckoutDTO> getAllPaginationWithCondition(String user, String bookCopy, String status, Pageable pageable) {
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
        Authority librarianAuthority = new Authority();
        librarianAuthority.setName("ROLE_LIBRARIAN");
        Authority adminAuthority = new Authority();
        adminAuthority.setName("ROLE_ADMIN");
        // if current user is librarian or an admin
        if (currentUser.getAuthorities().contains(librarianAuthority) || currentUser.getAuthorities().contains(adminAuthority)) {
            if (status.equalsIgnoreCase("borrowing")) {
                return checkoutRepository.findAllWithEndTimeNull(user, bookCopy, isReturned, pageable).map(checkoutMapper::toDto);
            }
            if (status.equalsIgnoreCase("all")) {
                return checkoutRepository.findAllWithCondition(user, bookCopy, pageable).map(checkoutMapper::toDto);
            }
            return checkoutRepository.findAllWithEndTimeNotNull(user, bookCopy, isReturned, pageable).map(checkoutMapper::toDto);
        }
        // if current user is reader
        if (status.equalsIgnoreCase("borrowing")) {
            return checkoutRepository
                .findAllWithEndTimeNull(currentUser.getId().toString(), bookCopy, isReturned, pageable)
                .map(checkoutMapper::toDto);
        }
        if (status.equalsIgnoreCase("all")) {
            return checkoutRepository.findAllWithCondition(currentUser.getId().toString(), bookCopy, pageable).map(checkoutMapper::toDto);
        }
        return checkoutRepository
            .findAllWithEndTimeNotNull(currentUser.getId().toString(), bookCopy, isReturned, pageable)
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
            notification.setUser(queue.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notification.setBookCopy(checkout.getBookCopy());
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
    }

    public void borrowBook(BorrowBookRequestDTO borrowBookRequestDTO) {
        User user = userRepository
            .findById(borrowBookRequestDTO.getUserId())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "idnotfound"));
        // nếu user không được activate thì không cho mượn
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }
        BookCopy bookCopy = bookCopyRepository
            .findById(borrowBookRequestDTO.getBookCopyId())
            .orElseThrow(() -> new BadRequestAlertException("Book Copy not found", "BookCopy", "idnotfound"));
        // nếu bản sao bị xóa hoặc bản gốc bị xóa thì không cho mượn
        if (bookCopy.getIsDeleted() || bookCopy.getBook().getIsDeleted()) {
            throw new BadRequestAlertException("Book Copy is deleted", "BookCopy", "bookcopyisdeleted");
        }

        Checkout checkout = checkoutRepository.findThatBookCopyIsBorrowed(bookCopy.getId()).orElse(null);
        // nếu bản sao đang được mượn (trong danh sách mượn trả là endtime = null)
        if (checkout != null) {
            throw new BadRequestAlertException("Book Copy is borrowed", "BookCopy", "bookcopyisborrowed");
        }
        Reservation reservation = reservationRepository.findThatBookCopyIsHold(bookCopy.getId()).orElse(null);
        // nếu bản sao đang được đặt trước
        if (reservation != null) {
            throw new BadRequestAlertException("Book Copy is hold", "BookCopy", "bookcopyishold");
        }
        Checkout newCheckout = new Checkout();
        newCheckout.setUser(user);
        newCheckout.setBookCopy(bookCopy);
        newCheckout.setStartTime(Instant.now());
        newCheckout.setIsReturned(false);
        checkoutRepository.save(newCheckout);
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
            notification.setUser(queue.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notification.setBookCopy(checkout.getBookCopy());
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
    }
}
