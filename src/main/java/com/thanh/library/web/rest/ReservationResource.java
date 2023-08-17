package com.thanh.library.web.rest;

import com.thanh.library.repository.ReservationRepository;
import com.thanh.library.service.ReservationService;
import com.thanh.library.service.dto.ReservationDTO;
import com.thanh.library.service.dto.request.HoldBookRequestDTO;
import com.thanh.library.util.LibraryHeaderUtil;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.thanh.library.domain.Reservation}.
 */
@RestController
@RequestMapping("/api")
public class ReservationResource {

    private final Logger log = LoggerFactory.getLogger(ReservationResource.class);

    private static final String ENTITY_NAME = "reservation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReservationService reservationService;

    private final ReservationRepository reservationRepository;

    public ReservationResource(ReservationService reservationService, ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody HoldBookRequestDTO requestDTO) throws URISyntaxException {
        log.debug("REST request to save Reservation : {}", requestDTO);
        ReservationDTO result = reservationService.save(requestDTO);
        return ResponseEntity
            .created(new URI("/api/reservations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservationDTO reservationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Reservation : {}, {}", id, reservationDTO);
        if (reservationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ReservationDTO result = reservationService.update(reservationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/reservations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReservationDTO> partialUpdateReservation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ReservationDTO reservationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Reservation partially : {}, {}", id, reservationDTO);
        if (reservationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, reservationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!reservationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReservationDTO> result = reservationService.partialUpdate(reservationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, reservationDTO.getId().toString())
        );
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload,
        @RequestParam(required = false, value = "user") String user,
        @RequestParam(required = false, value = "bookCopy") String bookCopy
    ) {
        log.debug("REST request to get a page of Reservations");
        Page<ReservationDTO> page = reservationService.getAllPaginationWithCondition(user, bookCopy, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        log.debug("REST request to get Reservation : {}", id);
        Optional<ReservationDTO> reservationDTO = reservationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(reservationDTO);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        log.debug("REST request to delete Reservation : {}", id);
        reservationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/reservations/{id}/borrow")
    public ResponseEntity<Void> borrowFromReservation(@PathVariable Long id) {
        Long bookCopyId = reservationService.borrowBook(id);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createBookCopyBorrowAlert(applicationName, true, bookCopyId.toString()))
            .build();
    }
}
