package com.thanh.library.web.rest;

import com.thanh.library.repository.BookCopyRepository;
import com.thanh.library.service.BookCopyService;
import com.thanh.library.service.dto.BookCopyDTO;
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
import org.springframework.http.HttpStatus;
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

    /**
     * {@code POST  /book-copies} : Create a new bookCopy.
     *
     * @param bookCopyDTO the bookCopyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bookCopyDTO, or with status {@code 400 (Bad Request)} if the bookCopy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PUT  /book-copies/:id} : Updates an existing bookCopy.
     *
     * @param id the id of the bookCopyDTO to save.
     * @param bookCopyDTO the bookCopyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCopyDTO,
     * or with status {@code 400 (Bad Request)} if the bookCopyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bookCopyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PATCH  /book-copies/:id} : Partial updates given fields of an existing bookCopy, field will ignore if it is null
     *
     * @param id the id of the bookCopyDTO to save.
     * @param bookCopyDTO the bookCopyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCopyDTO,
     * or with status {@code 400 (Bad Request)} if the bookCopyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the bookCopyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookCopyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code GET  /book-copies} : get all the bookCopies.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bookCopies in body.
     */
    @GetMapping("/book-copies")
    public ResponseEntity<List<BookCopyDTO>> getAllBookCopies(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of BookCopies");
        Page<BookCopyDTO> page;
        if (eagerload) {
            page = bookCopyService.findAllWithEagerRelationships(pageable);
        } else {
            page = bookCopyService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /book-copies/:id} : get the "id" bookCopy.
     *
     * @param id the id of the bookCopyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookCopyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/book-copies/{id}")
    public ResponseEntity<BookCopyDTO> getBookCopy(@PathVariable Long id) {
        log.debug("REST request to get BookCopy : {}", id);
        Optional<BookCopyDTO> bookCopyDTO = bookCopyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookCopyDTO);
    }

    /**
     * {@code DELETE  /book-copies/:id} : delete the "id" bookCopy.
     *
     * @param id the id of the bookCopyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/book-copies/{id}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id) {
        log.debug("REST request to delete BookCopy : {}", id);
        bookCopyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
