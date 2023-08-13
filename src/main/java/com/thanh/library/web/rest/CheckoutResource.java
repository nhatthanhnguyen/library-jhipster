package com.thanh.library.web.rest;

import com.thanh.library.domain.Checkout;
import com.thanh.library.repository.CheckoutRepository;
import com.thanh.library.service.CheckoutService;
import com.thanh.library.service.dto.CheckoutDTO;
import com.thanh.library.service.dto.request.BorrowBookRequestDTO;
import com.thanh.library.service.dto.request.ReturnBookRequestDTO;
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
 * REST controller for managing {@link com.thanh.library.domain.Checkout}.
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private static final String ENTITY_NAME = "checkout";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckoutService checkoutService;

    private final CheckoutRepository checkoutRepository;

    public CheckoutResource(CheckoutService checkoutService, CheckoutRepository checkoutRepository) {
        this.checkoutService = checkoutService;
        this.checkoutRepository = checkoutRepository;
    }

    @PostMapping("/checkouts")
    public ResponseEntity<CheckoutDTO> createCheckout(@RequestBody CheckoutDTO checkoutDTO) throws URISyntaxException {
        log.debug("REST request to save Checkout : {}", checkoutDTO);
        if (checkoutDTO.getId() != null) {
            throw new BadRequestAlertException("A new checkout cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CheckoutDTO result = checkoutService.save(checkoutDTO);
        return ResponseEntity
            .created(new URI("/api/checkouts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/checkouts/{id}")
    public ResponseEntity<CheckoutDTO> updateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CheckoutDTO checkoutDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Checkout : {}, {}", id, checkoutDTO);
        if (checkoutDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkoutDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CheckoutDTO result = checkoutService.update(checkoutDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, checkoutDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/checkouts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CheckoutDTO> partialUpdateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CheckoutDTO checkoutDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Checkout partially : {}, {}", id, checkoutDTO);
        if (checkoutDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkoutDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CheckoutDTO> result = checkoutService.partialUpdate(checkoutDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, checkoutDTO.getId().toString())
        );
    }

    @GetMapping("/checkouts")
    public ResponseEntity<List<CheckoutDTO>> getAllCheckouts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Checkouts");
        Page<CheckoutDTO> page = checkoutService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/checkouts/{id}")
    public ResponseEntity<CheckoutDTO> getCheckout(@PathVariable Long id) {
        log.debug("REST request to get Checkout : {}", id);
        Optional<CheckoutDTO> checkoutDTO = checkoutService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checkoutDTO);
    }

    @DeleteMapping("/checkouts/{id}")
    public ResponseEntity<Void> deleteCheckout(@PathVariable Long id) {
        log.debug("REST request to delete Checkout : {}", id);
        checkoutService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/checkouts/borrow")
    public ResponseEntity<Void> borrowBook(@RequestBody BorrowBookRequestDTO borrowBookRequestDTO) {
        log.debug(
            "REST request to borrow Book {} of User {} ",
            borrowBookRequestDTO.getBookCopy().getBook(),
            borrowBookRequestDTO.getUser().getId()
        );
        checkoutService.borrowBook(borrowBookRequestDTO);
        String param = borrowBookRequestDTO.getBookCopy().getBook().getId().toString();
        return ResponseEntity.noContent().headers(LibraryHeaderUtil.createBookCopyBorrowAlert(applicationName, true, param)).build();
    }

    @PutMapping("/checkouts/return")
    public ResponseEntity<Void> returnBook(@RequestBody ReturnBookRequestDTO returnBookRequestDTO) {
        Checkout checkout = checkoutRepository
            .findById(returnBookRequestDTO.getId())
            .orElseThrow(() -> new BadRequestAlertException("Checkout not found", "Checkout", "idnotfound"));
        String param = checkout.getId().toString();
        checkoutService.returnBook(returnBookRequestDTO);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createBookCopyReturnAlert(applicationName, true, param, returnBookRequestDTO.isSuccess()))
            .build();
    }

    @PostMapping("/checkouts/books/{id}/wait")
    public ResponseEntity<Void> addToQueue(@PathVariable("id") Long id) {
        checkoutService.addToQueue(id);
        return ResponseEntity.noContent().headers(LibraryHeaderUtil.createBookWaitAlert(applicationName, true, id.toString())).build();
    }
}
