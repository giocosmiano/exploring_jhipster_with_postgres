package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.StaffRepository;
import com.giocosmiano.dvdrental.service.StaffService;
import com.giocosmiano.dvdrental.service.dto.StaffDTO;
import com.giocosmiano.dvdrental.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.Staff}.
 */
@RestController
@RequestMapping("/api")
public class StaffResource {

    private final Logger log = LoggerFactory.getLogger(StaffResource.class);

    private static final String ENTITY_NAME = "staff";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StaffService staffService;

    private final StaffRepository staffRepository;

    public StaffResource(StaffService staffService, StaffRepository staffRepository) {
        this.staffService = staffService;
        this.staffRepository = staffRepository;
    }

    /**
     * {@code POST  /staff} : Create a new staff.
     *
     * @param staffDTO the staffDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new staffDTO, or with status {@code 400 (Bad Request)} if the staff has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/staff")
    public Mono<ResponseEntity<StaffDTO>> createStaff(@Valid @RequestBody StaffDTO staffDTO) throws URISyntaxException {
        log.debug("REST request to save Staff : {}", staffDTO);
        if (staffDTO.getId() != null) {
            throw new BadRequestAlertException("A new staff cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return staffService
            .save(staffDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/staff/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /staff/:id} : Updates an existing staff.
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/staff/{id}")
    public Mono<ResponseEntity<StaffDTO>> updateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Staff : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return staffRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return staffService
                    .save(staffDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /staff/:id} : Partial updates given fields of an existing staff, field will ignore if it is null
     *
     * @param id the id of the staffDTO to save.
     * @param staffDTO the staffDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated staffDTO,
     * or with status {@code 400 (Bad Request)} if the staffDTO is not valid,
     * or with status {@code 404 (Not Found)} if the staffDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the staffDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/staff/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StaffDTO>> partialUpdateStaff(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StaffDTO staffDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Staff partially : {}, {}", id, staffDTO);
        if (staffDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, staffDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return staffRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StaffDTO> result = staffService.partialUpdate(staffDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /staff} : get all the staff.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of staff in body.
     */
    @GetMapping("/staff")
    public Mono<ResponseEntity<List<StaffDTO>>> getAllStaff(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Staff");
        return staffService
            .countAll()
            .zipWith(staffService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /staff/:id} : get the "id" staff.
     *
     * @param id the id of the staffDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the staffDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/staff/{id}")
    public Mono<ResponseEntity<StaffDTO>> getStaff(@PathVariable Long id) {
        log.debug("REST request to get Staff : {}", id);
        Mono<StaffDTO> staffDTO = staffService.findOne(id);
        return ResponseUtil.wrapOrNotFound(staffDTO);
    }

    /**
     * {@code DELETE  /staff/:id} : delete the "id" staff.
     *
     * @param id the id of the staffDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/staff/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteStaff(@PathVariable Long id) {
        log.debug("REST request to delete Staff : {}", id);
        return staffService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
