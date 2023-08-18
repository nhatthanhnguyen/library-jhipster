package com.thanh.library.web.rest;

import com.thanh.library.repository.BookRepository;
import com.thanh.library.security.SecurityUtils;
import com.thanh.library.service.BookService;
import com.thanh.library.service.dto.BookDTO;
import com.thanh.library.service.dto.request.HoldBookRequestDTO;
import com.thanh.library.service.dto.request.WaitBookRequestDTO;
import com.thanh.library.util.LibraryHeaderUtil;
import com.thanh.library.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.thanh.library.domain.Book}.
 */
@RestController
@RequestMapping("/api")
public class BookResource {

    private final Logger log = LoggerFactory.getLogger(BookResource.class);

    private static final String ENTITY_NAME = "book";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BookService bookService;

    private final BookRepository bookRepository;

    public BookResource(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) throws URISyntaxException {
        log.debug("REST request to save Book : {}", bookDTO);
        if (bookDTO.getId() != null) {
            throw new BadRequestAlertException("A new book cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BookDTO result = bookService.save(bookDTO);
        return ResponseEntity
            .created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookDTO> updateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BookDTO bookDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Book : {}, {}", id, bookDTO);
        if (bookDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BookDTO result = bookService.update(bookDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/books/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BookDTO> partialUpdateBook(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BookDTO bookDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Book partially : {}, {}", id, bookDTO);
        if (bookDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bookDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookDTO> result = bookService.partialUpdate(bookDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bookDTO.getId().toString())
        );
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getAllBooksPagination(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload,
        @RequestParam(required = false, defaultValue = "") String search
    ) {
        log.debug("REST request to get a page of Books");
        Page<BookDTO> page;
        if (SecurityUtils.hasCurrentUserThisAuthority("ROLE_LIBRARIAN")) {
            page = bookService.getAllPagination(search, pageable);
        } else {
            page = bookService.getAllAvailablePagination(search, pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/books/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> getAllBooksByCategoryPagination(
        @PathVariable("categoryId") Long categoryId,
        @ParameterObject Pageable pageable
    ) {
        Page<BookDTO> page = bookService.getAllPaginationByCategory(categoryId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/books/publisher/{publisherId}")
    public ResponseEntity<List<BookDTO>> getAllBooksByPublisherPagination(
        @PathVariable("publisherId") Long publisherId,
        @ParameterObject Pageable pageable
    ) {
        Page<BookDTO> page = bookService.getAllPaginationByPublisher(publisherId, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/books/all")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.getAllBooksAvailable());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        log.debug("REST request to get Book : {}", id);
        Optional<BookDTO> bookDTO = bookService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookDTO);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("REST request to delete Book : {}", id);
        bookService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PutMapping("/books/{id}/restore")
    public ResponseEntity<Void> restoreBook(@PathVariable Long id) {
        log.debug("REST request to restore Book : {}", id);
        bookService.restore(id);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createEntityRestoreAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/books/hold")
    public ResponseEntity<Void> holdBook(@RequestBody HoldBookRequestDTO requestDTO) {
        log.debug("REST request to hold Book : {}", requestDTO.getBookId());
        Long bookCopyId = bookService.holdBookByCurrentUser(requestDTO);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createBookHoldAlert(applicationName, true, bookCopyId.toString()))
            .build();
    }

    @PostMapping("/books/wait")
    public ResponseEntity<Void> waitBook(@RequestBody WaitBookRequestDTO requestDTO) {
        log.debug("REST request to add Book {} to queue", requestDTO.getBookId());
        bookService.addToQueue(requestDTO);
        return ResponseEntity
            .noContent()
            .headers(LibraryHeaderUtil.createBookWaitAlert(applicationName, true, requestDTO.getBookId().toString()))
            .build();
    }
}
