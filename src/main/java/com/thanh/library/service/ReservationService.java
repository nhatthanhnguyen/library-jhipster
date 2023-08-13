package com.thanh.library.service;

import com.thanh.library.domain.Checkout;
import com.thanh.library.domain.Reservation;
import com.thanh.library.repository.CheckoutRepository;
import com.thanh.library.repository.ReservationRepository;
import com.thanh.library.service.dto.ReservationDTO;
import com.thanh.library.service.mapper.ReservationMapper;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
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

    private final ReservationMapper reservationMapper;

    public ReservationService(
        ReservationRepository reservationRepository,
        CheckoutRepository checkoutRepository,
        ReservationMapper reservationMapper
    ) {
        this.reservationRepository = reservationRepository;
        this.checkoutRepository = checkoutRepository;
        this.reservationMapper = reservationMapper;
    }

    public ReservationDTO save(ReservationDTO reservationDTO) {
        log.debug("Request to save Reservation : {}", reservationDTO);
        Reservation reservation = reservationMapper.toEntity(reservationDTO);
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
    public Page<ReservationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Reservations");
        return reservationRepository.findAll(pageable).map(reservationMapper::toDto);
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
