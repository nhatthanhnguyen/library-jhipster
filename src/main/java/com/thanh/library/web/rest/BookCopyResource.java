package com.thanh.library.web.rest;

import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.service.BookCopyService;
import com.thanh.library.service.dto.BookCopyDTO;
import com.thanh.library.util.LibraryHeaderUtil;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.api.annotations.ParameterObject;
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
 * REST controller for managing {@link com.thanh.library.domain.BookCopy}.
 */
@RestController
@RequestMapping("/api")
public class BookCopyResource {

    private final Logger log = LoggerFactory.getLogger(BookCopyResource.class);

    private static final String ENTITY_NAME = "bookCopy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookCopyService bookCopyService;

    private final BookCopyRepository bookCopyRepository;

    public BookCopyResource(BookCopyService bookCopyService, BookCopyRepository bookCopyRepository) {
        this.bookCopyService = bookCopyService;
        this.bookCopyRepository = bookCopyRepository;
    }

    @PostMapping("/book-copies")
    public ResponseEntity<BookCopyDTO> createBookCopy(@RequestBody BookCopyDTO bookCopyDTO) throws URISyntaxException {
        log.debug("REST request to save BookCopy : {}", bookCopyDTO);
        if (bookCopyDTO.getId() != null) {
            throw new BadRequestAlertException("A new bookCopy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookCopyDTO result = bookCopyService.save(bookCopyDTO);
        return ResponseEntity
            .created(new URI("/api/book-copies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/book-copies/{id}")
    public ResponseEntity<BookCopyDTO> updateBookCopy(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookCopyDTO bookCopyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BookCopy : {}, {}", id, bookCopyDTO);
        if (bookCopyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCopyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCopyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BookCopyDTO result = bookCopyService.update(bookCopyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCopyDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/book-copies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookCopyDTO> partialUpdateBookCopy(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BookCopyDTO bookCopyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BookCopy partially : {}, {}", id, bookCopyDTO);
        if (bookCopyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookCopyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCopyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookCopyDTO> result = bookCopyService.partialUpdate(bookCopyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookCopyDTO.getId().toString())
        );
    }

    @GetMapping("/book-copies")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopiesPagination(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of BookCopies");
        Page<BookCopyDTO> page;
        if (eagerload) {
            page = bookCopyService.findAllWithEagerRelationships(pageable);
        } else {
            page = bookCopyService.getAllBookCopiesPagination(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/book-copies/book/{bookId}")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopiesPaginationByBook(
        @PathVariable("bookId") Long bookId,
        @ParameterObject Pageable pageable
    ) {
        Page<BookCopyDTO> page = bookCopyService.getAllBookCopiesPaginationByBook(bookId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/book-copies/all")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopies() {
        return ResponseEntity.ok().body(bookCopyService.getAllAvailableBookCopies());
    }

    @GetMapping("/book-copies/{id}")
    public ResponseEntity<BookCopyDTO> getBookCopy(@PathVariable Long id) {
        log.debug("REST request to get BookCopy : {}", id);
        Optional<BookCopyDTO> bookCopyDTO = bookCopyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookCopyDTO);
    }

    @DeleteMapping("/book-copies/{id}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id) {
        log.debug("REST request to delete BookCopy : {}", id);
        bookCopyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PutMapping("/book-copies/{id}/restore")
    public ResponseEntity<Void> restoreBookCopy(@PathVariable("id") Long id) {
        bookCopyService.restore(id);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createEntityRestoreAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
