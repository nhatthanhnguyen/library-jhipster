package com.thanh.library.service;

import com.thanh.library.domain.*;
import com.thanh.library.domain.enumeration.Type;
import com.thanh.library.repository.*;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.dto.ReservationDTO;
import com.thanh.library.service.dto.request.HoldBookRequestDTO;
import com.thanh.library.service.mapper.ReservationMapper;
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
 * Service Implementation for managing {@link Reservation}.
 */
@Service
@Transactional
public class ReservationService {

    private final Logger log = LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;

    private final CheckoutRepository checkoutRepository;

    private final QueueRepository queueRepository;

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    private final BookCopyRepository bookCopyRepository;

    private final ReservationMapper reservationMapper;

    private final MailService mailService;

    public ReservationService(
        ReservationRepository reservationRepository,
        CheckoutRepository checkoutRepository,
        QueueRepository queueRepository,
        NotificationRepository notificationRepository,
        UserRepository userRepository,
        BookRepository bookRepository,
        BookCopyRepository bookCopyRepository,
        ReservationMapper reservationMapper,
        MailService mailService
    ) {
        this.reservationRepository = reservationRepository;
        this.checkoutRepository = checkoutRepository;
        this.queueRepository = queueRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.reservationMapper = reservationMapper;
        this.mailService = mailService;
    }

    public ReservationDTO save(HoldBookRequestDTO requestDTO) {
        log.debug("Request to save Reservation : {}", requestDTO);
        User user = userRepository
            .findById(requestDTO.getUserId())
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "idnotfound"));
        if (!user.isActivated()) {
            throw new BadRequestAlertException("User is not activated", "User", "usernotactivated");
        }
        Book book = bookRepository
            .findById(requestDTO.getBookId())
            .orElseThrow(() -> new BadRequestAlertException("Book not found", "Book", "idnotfound"));
        if (book.getIsDeleted()) {
            throw new BadRequestAlertException("Book is deleted", "Book", "bookisdeleted");
        }

        List<BookCopy> bookCopies = bookCopyRepository.findBookCopiesAvailableByBookId(requestDTO.getBookId());
        if (bookCopies.isEmpty()) {
            throw new BadRequestAlertException("Book Copy is not available", "BookCopy", "bookcopynotavailable");
        }
        Reservation reservation = new Reservation();
        reservation.setStartTime(Instant.now());
        reservation.setUser(user);
        reservation.setBookCopy(bookCopies.get(0));
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    public ReservationDTO update(ReservationDTO reservationDTO) {
        log.debug("Request to update Reservation : {}", reservationDTO);
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toDto(reservation);
    }

    public Optional<ReservationDTO> partialUpdate(ReservationDTO reservationDTO) {
        log.debug("Request to partially update Reservation : {}", reservationDTO);

        return reservationRepository
            .findById(reservationDTO.getId())
            .map(existingReservation -> {
                reservationMapper.partialUpdate(existingReservation, reservationDTO);

                return existingReservation;
            })
            .map(reservationRepository::save)
            .map(reservationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ReservationDTO> getAllPagination(Pageable pageable) {
        String login = SecurityUtils
            .getCurrentUserLogin()
            .orElseThrow(() -> new BadRequestAlertException("Login not found", "User", "loginnotfound"));
        User currentUser = userRepository
            .findOneByLogin(login)
            .orElseThrow(() -> new BadRequestAlertException("User not found", "User", "usernotfound"));
        Authority librarianRole = new Authority();
        librarianRole.setName("ROLE_LIBRARIAN");
        if (currentUser.getAuthorities().contains(librarianRole)) {
            return reservationRepository.findAll(pageable).map(reservationMapper::toDto);
        }
        return reservationRepository.getAllByUserPagination(currentUser.getId(), pageable).map(reservationMapper::toDto);
    }

    public Page<ReservationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reservationRepository.findAllWithEagerRelationships(pageable).map(reservationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReservationDTO> findOne(Long id) {
        log.debug("Request to get Reservation : {}", id);
        return reservationRepository.findOneWithEagerRelationships(id).map(reservationMapper::toDto);
    }

    public void delete(Long id) {
        log.debug("Request to delete Reservation : {}", id);
        Reservation reservation = reservationRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Reservation not found", "Reservation", "idnotfound"));
        Book book = reservation.getBookCopy().getBook();
        List<Queue> queues = queueRepository.findByBookId(book.getId());
        queues.forEach(queue -> {
            queueRepository.deleteById(queue.getId());
            Notification notification = new Notification();
            notification.setUser(reservation.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);
            mailService.sendBookRequestIsAvailable(notification.getUser(), notification.getBookCopy().getBook());
        });
        reservationRepository.deleteById(id);
    }

    public Long borrowBook(Long id) {
        Reservation reservation = reservationRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Reservation not found", "Reservation", "idnotfound"));
        Instant currentTime = Instant.now();
        reservation.setEndTime(currentTime);
        Checkout checkout = new Checkout();
        checkout.setBookCopy(reservation.getBookCopy());
        checkout.setUser(reservation.getUser());
        checkout.setIsReturned(false);
        checkout.setStartTime(currentTime);
        checkoutRepository.save(checkout);
        return reservation.getBookCopy().getId();
    }
}
