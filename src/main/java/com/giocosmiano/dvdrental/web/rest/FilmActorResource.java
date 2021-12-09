package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.FilmActorRepository;
import com.giocosmiano.dvdrental.service.FilmActorService;
import com.giocosmiano.dvdrental.service.dto.FilmActorDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.FilmActor}.
 */
@RestController
@RequestMapping("/api")
public class FilmActorResource {

    private final Logger log = LoggerFactory.getLogger(FilmActorResource.class);

    private static final String ENTITY_NAME = "filmActor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FilmActorService filmActorService;

    private final FilmActorRepository filmActorRepository;

    public FilmActorResource(FilmActorService filmActorService, FilmActorRepository filmActorRepository) {
        this.filmActorService = filmActorService;
        this.filmActorRepository = filmActorRepository;
    }

    /**
     * {@code POST  /film-actors} : Create a new filmActor.
     *
     * @param filmActorDTO the filmActorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new filmActorDTO, or with status {@code 400 (Bad Request)} if the filmActor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/film-actors")
    public Mono<ResponseEntity<FilmActorDTO>> createFilmActor(@Valid @RequestBody FilmActorDTO filmActorDTO) throws URISyntaxException {
        log.debug("REST request to save FilmActor : {}", filmActorDTO);
        if (filmActorDTO.getId() != null) {
            throw new BadRequestAlertException("A new filmActor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return filmActorService
            .save(filmActorDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/film-actors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /film-actors/:id} : Updates an existing filmActor.
     *
     * @param id the id of the filmActorDTO to save.
     * @param filmActorDTO the filmActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmActorDTO,
     * or with status {@code 400 (Bad Request)} if the filmActorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the filmActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/film-actors/{id}")
    public Mono<ResponseEntity<FilmActorDTO>> updateFilmActor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FilmActorDTO filmActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FilmActor : {}, {}", id, filmActorDTO);
        if (filmActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmActorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return filmActorService
                    .save(filmActorDTO)
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
     * {@code PATCH  /film-actors/:id} : Partial updates given fields of an existing filmActor, field will ignore if it is null
     *
     * @param id the id of the filmActorDTO to save.
     * @param filmActorDTO the filmActorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmActorDTO,
     * or with status {@code 400 (Bad Request)} if the filmActorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the filmActorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the filmActorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/film-actors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FilmActorDTO>> partialUpdateFilmActor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FilmActorDTO filmActorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FilmActor partially : {}, {}", id, filmActorDTO);
        if (filmActorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmActorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmActorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FilmActorDTO> result = filmActorService.partialUpdate(filmActorDTO);

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
     * {@code GET  /film-actors} : get all the filmActors.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of filmActors in body.
     */
    @GetMapping("/film-actors")
    public Mono<ResponseEntity<List<FilmActorDTO>>> getAllFilmActors(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of FilmActors");
        return filmActorService
            .countAll()
            .zipWith(filmActorService.findAll(pageable).collectList())
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
     * {@code GET  /film-actors/:id} : get the "id" filmActor.
     *
     * @param id the id of the filmActorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the filmActorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/film-actors/{id}")
    public Mono<ResponseEntity<FilmActorDTO>> getFilmActor(@PathVariable Long id) {
        log.debug("REST request to get FilmActor : {}", id);
        Mono<FilmActorDTO> filmActorDTO = filmActorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(filmActorDTO);
    }

    /**
     * {@code DELETE  /film-actors/:id} : delete the "id" filmActor.
     *
     * @param id the id of the filmActorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/film-actors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFilmActor(@PathVariable Long id) {
        log.debug("REST request to delete FilmActor : {}", id);
        return filmActorService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
