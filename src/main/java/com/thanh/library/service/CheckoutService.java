package com.thanh.library.service;

import com.thanh.library.domain.*;
import com.thanh.library.domain.enumeration.Type;
import com.thanh.library.repository.*;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.dto.CheckoutDTO;
import com.thanh.library.service.dto.request.BorrowBookRequestDTO;
import com.thanh.library.service.dto.request.ReturnBookRequestDTO;
import com.thanh.library.service.dto.response.ResponseMessageDTO;
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

    public CheckoutService(
        CheckoutRepository checkoutRepository,
        UserRepository userRepository,
        BookCopyRepository bookCopyRepository,
        ReservationRepository reservationRepository,
        QueueRepository queueRepository,
        NotificationRepository notificationRepository,
        CheckoutMapper checkoutMapper
    ) {
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.notificationRepository = notificationRepository;
        this.checkoutMapper = checkoutMapper;
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
    public Page<CheckoutDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Checkouts");
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Login not found", "User", "loginnotfound"));
        User user = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        //        Authority roleAdmin = new Authority();
        Authority roleLibrarian = new Authority();
        //        roleAdmin.setName("ROLE_ADMIN");
        roleLibrarian.setName("ROLE_LIBRARIAN");
        /*if (user.getAuthorities().contains(roleAdmin)) {
            throw new BadRequestAlertException("You does not has permission", "Permission", "donothavepermission");
        }*/
        if (user.getAuthorities().contains(roleLibrarian)) {
            return checkoutRepository.findAll(pageable).map(checkoutMapper::toDto);
        }
        return checkoutRepository.findAllByCurrentUser(user.getId(), pageable).map(checkoutMapper::toDto);
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
        checkoutRepository.deleteById(id);
    }

    public ResponseMessageDTO borrowBook(BorrowBookRequestDTO borrowBookRequestDTO) {
        User user = userRepository
            .findById(borrowBookRequestDTO.getUserId())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "idnotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }
        BookCopy bookCopy = bookCopyRepository
            .findById(borrowBookRequestDTO.getBookCopyId())
            .orElseThrow(() -> new BadRequestAlertException("Book Copy not found", "BookCopy", "idnotfound"));
        if (bookCopy.getIsDeleted()) {
            throw new BadRequestAlertException("Book Copy is deleted", "BookCopy", "bookcopyisdeleted");
        }

        List<Checkout> checkouts = checkoutRepository.findAllThatBookCopyIsBorrowed(borrowBookRequestDTO.getBookCopyId());
        List<Reservation> reservations = reservationRepository.findAllThatBookCopyIsBorrowed(borrowBookRequestDTO.getBookCopyId());
        if (!checkouts.isEmpty() || !reservations.isEmpty()) {
            throw new BadRequestAlertException("Book Copy is not available", "BookCopy", "bookcopyisnotavailable");
        }
        Checkout checkout = new Checkout();
        checkout.setUser(user);
        checkout.setBookCopy(bookCopy);
        checkout.setStartTime(Instant.now());
        checkout.setIsReturned(false);
        checkoutRepository.save(checkout);
        return new ResponseMessageDTO(200, "Borrowed book successfully", Instant.now());
    }

    public ResponseMessageDTO returnBook(ReturnBookRequestDTO returnBookRequestDTO) {
        Checkout checkout = checkoutRepository
            .findById(returnBookRequestDTO.getCheckoutId())
            .orElseThrow(() -> new BadRequestAlertException("Checkout not found", "Checkout", "checkoutnotfound"));
        if (checkout.getIsReturned()) {
            throw new BadRequestAlertException("Book Copy is returned", "BookCopy", "bookcopyisreturned");
        }
        checkout.setIsReturned(returnBookRequestDTO.isReturnSuccess());
        checkout.setEndTime(Instant.now());
        checkoutRepository.save(checkout);
        Book book = checkout.getBookCopy().getBook();
        List<Queue> queues = queueRepository.findByBookId(book.getId());
        queues.forEach(queue -> {
            //            queueRepository.deleteById(queue.getId());
            Notification notification = new Notification();
            notification.setUser(checkout.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);
            // call mail service
        });
        return new ResponseMessageDTO(200, "Return book successfully", Instant.now());
    }
}
