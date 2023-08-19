package com.thanh.library.web.rest;

import com.thanh.library.repository.PublisherRepository;
import com.thanh.library.service.PublisherService;
import com.thanh.library.service.dto.PublisherDTO;
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
 * REST controller for managing {@link com.thanh.library.domain.Publisher}.
 */
@RestController
@RequestMapping("/api")
public class PublisherResource {

    private final Logger log = LoggerFactory.getLogger(PublisherResource.class);

    private static final String ENTITY_NAME = "publisher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublisherService publisherService;

    private final PublisherRepository publisherRepository;

    public PublisherResource(PublisherService publisherService, PublisherRepository publisherRepository) {
        this.publisherService = publisherService;
        this.publisherRepository = publisherRepository;
    }

    @PostMapping("/publishers")
    public ResponseEntity<PublisherDTO> createPublisher(@Valid @RequestBody PublisherDTO publisherDTO) throws URISyntaxException {
        log.debug("REST request to save Publisher : {}", publisherDTO);
        if (publisherDTO.getId() != null) {
            throw new BadRequestAlertException("A new publisher cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PublisherDTO result = publisherService.save(publisherDTO);
        return ResponseEntity
            .created(new URI("/api/publishers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/publishers/{id}")
    public ResponseEntity<PublisherDTO> updatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Publisher : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PublisherDTO result = publisherService.update(publisherDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/publishers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublisherDTO> partialUpdatePublisher(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PublisherDTO publisherDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Publisher partially : {}, {}", id, publisherDTO);
        if (publisherDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publisherDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publisherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublisherDTO> result = publisherService.partialUpdate(publisherDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publisherDTO.getId().toString())
        );
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<PublisherDTO>> getAllPublishersPagination(
        @RequestParam(required = false) String search,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of Publishers");
        Page<PublisherDTO> page = publisherService.getAllPagination(search, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/publishers/all")
    public ResponseEntity<List<PublisherDTO>> getAllPublishers() {
        return ResponseEntity.ok().body(publisherService.getAll());
    }

    @GetMapping("/publishers/{id}")
    public ResponseEntity<PublisherDTO> getPublisher(@PathVariable Long id) {
        log.debug("REST request to get Publisher : {}", id);
        Optional<PublisherDTO> publisherDTO = publisherService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publisherDTO);
    }

    @DeleteMapping("/publishers/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        log.debug("REST request to delete Publisher : {}", id);
        publisherService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
