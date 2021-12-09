package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.StoreRepository;
import com.giocosmiano.dvdrental.service.StoreService;
import com.giocosmiano.dvdrental.service.dto.StoreDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.Store}.
 */
@RestController
@RequestMapping("/api")
public class StoreResource {

    private final Logger log = LoggerFactory.getLogger(StoreResource.class);

    private static final String ENTITY_NAME = "store";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StoreService storeService;

    private final StoreRepository storeRepository;

    public StoreResource(StoreService storeService, StoreRepository storeRepository) {
        this.storeService = storeService;
        this.storeRepository = storeRepository;
    }

    /**
     * {@code POST  /stores} : Create a new store.
     *
     * @param storeDTO the storeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new storeDTO, or with status {@code 400 (Bad Request)} if the store has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stores")
    public Mono<ResponseEntity<StoreDTO>> createStore(@Valid @RequestBody StoreDTO storeDTO) throws URISyntaxException {
        log.debug("REST request to save Store : {}", storeDTO);
        if (storeDTO.getId() != null) {
            throw new BadRequestAlertException("A new store cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return storeService
            .save(storeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/stores/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /stores/:id} : Updates an existing store.
     *
     * @param id the id of the storeDTO to save.
     * @param storeDTO the storeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storeDTO,
     * or with status {@code 400 (Bad Request)} if the storeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the storeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stores/{id}")
    public Mono<ResponseEntity<StoreDTO>> updateStore(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StoreDTO storeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Store : {}, {}", id, storeDTO);
        if (storeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return storeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return storeService
                    .save(storeDTO)
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
     * {@code PATCH  /stores/:id} : Partial updates given fields of an existing store, field will ignore if it is null
     *
     * @param id the id of the storeDTO to save.
     * @param storeDTO the storeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated storeDTO,
     * or with status {@code 400 (Bad Request)} if the storeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the storeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the storeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stores/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<StoreDTO>> partialUpdateStore(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StoreDTO storeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Store partially : {}, {}", id, storeDTO);
        if (storeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, storeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return storeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<StoreDTO> result = storeService.partialUpdate(storeDTO);

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
     * {@code GET  /stores} : get all the stores.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stores in body.
     */
    @GetMapping("/stores")
    public Mono<ResponseEntity<List<StoreDTO>>> getAllStores(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Stores");
        return storeService
            .countAll()
            .zipWith(storeService.findAll(pageable).collectList())
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
     * {@code GET  /stores/:id} : get the "id" store.
     *
     * @param id the id of the storeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the storeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stores/{id}")
    public Mono<ResponseEntity<StoreDTO>> getStore(@PathVariable Long id) {
        log.debug("REST request to get Store : {}", id);
        Mono<StoreDTO> storeDTO = storeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(storeDTO);
    }

    /**
     * {@code DELETE  /stores/:id} : delete the "id" store.
     *
     * @param id the id of the storeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stores/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteStore(@PathVariable Long id) {
        log.debug("REST request to delete Store : {}", id);
        return storeService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
