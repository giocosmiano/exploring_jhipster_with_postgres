package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.FilmCategoryRepository;
import com.giocosmiano.dvdrental.service.FilmCategoryService;
import com.giocosmiano.dvdrental.service.dto.FilmCategoryDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.FilmCategory}.
 */
@RestController
@RequestMapping("/api")
public class FilmCategoryResource {

    private final Logger log = LoggerFactory.getLogger(FilmCategoryResource.class);

    private static final String ENTITY_NAME = "filmCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FilmCategoryService filmCategoryService;

    private final FilmCategoryRepository filmCategoryRepository;

    public FilmCategoryResource(FilmCategoryService filmCategoryService, FilmCategoryRepository filmCategoryRepository) {
        this.filmCategoryService = filmCategoryService;
        this.filmCategoryRepository = filmCategoryRepository;
    }

    /**
     * {@code POST  /film-categories} : Create a new filmCategory.
     *
     * @param filmCategoryDTO the filmCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new filmCategoryDTO, or with status {@code 400 (Bad Request)} if the filmCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/film-categories")
    public Mono<ResponseEntity<FilmCategoryDTO>> createFilmCategory(@Valid @RequestBody FilmCategoryDTO filmCategoryDTO)
        throws URISyntaxException {
        log.debug("REST request to save FilmCategory : {}", filmCategoryDTO);
        if (filmCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new filmCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return filmCategoryService
            .save(filmCategoryDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/film-categories/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /film-categories/:id} : Updates an existing filmCategory.
     *
     * @param id the id of the filmCategoryDTO to save.
     * @param filmCategoryDTO the filmCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the filmCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the filmCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/film-categories/{id}")
    public Mono<ResponseEntity<FilmCategoryDTO>> updateFilmCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FilmCategoryDTO filmCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FilmCategory : {}, {}", id, filmCategoryDTO);
        if (filmCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return filmCategoryService
                    .save(filmCategoryDTO)
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
     * {@code PATCH  /film-categories/:id} : Partial updates given fields of an existing filmCategory, field will ignore if it is null
     *
     * @param id the id of the filmCategoryDTO to save.
     * @param filmCategoryDTO the filmCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the filmCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the filmCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the filmCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/film-categories/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FilmCategoryDTO>> partialUpdateFilmCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FilmCategoryDTO filmCategoryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FilmCategory partially : {}, {}", id, filmCategoryDTO);
        if (filmCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmCategoryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FilmCategoryDTO> result = filmCategoryService.partialUpdate(filmCategoryDTO);

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
     * {@code GET  /film-categories} : get all the filmCategories.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of filmCategories in body.
     */
    @GetMapping("/film-categories")
    public Mono<ResponseEntity<List<FilmCategoryDTO>>> getAllFilmCategories(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of FilmCategories");
        return filmCategoryService
            .countAll()
            .zipWith(filmCategoryService.findAll(pageable).collectList())
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
     * {@code GET  /film-categories/:id} : get the "id" filmCategory.
     *
     * @param id the id of the filmCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the filmCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/film-categories/{id}")
    public Mono<ResponseEntity<FilmCategoryDTO>> getFilmCategory(@PathVariable Long id) {
        log.debug("REST request to get FilmCategory : {}", id);
        Mono<FilmCategoryDTO> filmCategoryDTO = filmCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(filmCategoryDTO);
    }

    /**
     * {@code DELETE  /film-categories/:id} : delete the "id" filmCategory.
     *
     * @param id the id of the filmCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/film-categories/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFilmCategory(@PathVariable Long id) {
        log.debug("REST request to delete FilmCategory : {}", id);
        return filmCategoryService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
