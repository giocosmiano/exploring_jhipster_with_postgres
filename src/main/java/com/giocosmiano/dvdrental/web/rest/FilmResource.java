package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.FilmRepository;
import com.giocosmiano.dvdrental.service.FilmService;
import com.giocosmiano.dvdrental.service.dto.FilmDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.Film}.
 */
@RestController
@RequestMapping("/api")
public class FilmResource {

    private final Logger log = LoggerFactory.getLogger(FilmResource.class);

    private static final String ENTITY_NAME = "film";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FilmService filmService;

    private final FilmRepository filmRepository;

    public FilmResource(FilmService filmService, FilmRepository filmRepository) {
        this.filmService = filmService;
        this.filmRepository = filmRepository;
    }

    /**
     * {@code POST  /films} : Create a new film.
     *
     * @param filmDTO the filmDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new filmDTO, or with status {@code 400 (Bad Request)} if the film has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/films")
    public Mono<ResponseEntity<FilmDTO>> createFilm(@Valid @RequestBody FilmDTO filmDTO) throws URISyntaxException {
        log.debug("REST request to save Film : {}", filmDTO);
        if (filmDTO.getId() != null) {
            throw new BadRequestAlertException("A new film cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return filmService
            .save(filmDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/films/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /films/:id} : Updates an existing film.
     *
     * @param id the id of the filmDTO to save.
     * @param filmDTO the filmDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmDTO,
     * or with status {@code 400 (Bad Request)} if the filmDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the filmDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/films/{id}")
    public Mono<ResponseEntity<FilmDTO>> updateFilm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FilmDTO filmDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Film : {}, {}", id, filmDTO);
        if (filmDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return filmService
                    .save(filmDTO)
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
     * {@code PATCH  /films/:id} : Partial updates given fields of an existing film, field will ignore if it is null
     *
     * @param id the id of the filmDTO to save.
     * @param filmDTO the filmDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated filmDTO,
     * or with status {@code 400 (Bad Request)} if the filmDTO is not valid,
     * or with status {@code 404 (Not Found)} if the filmDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the filmDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/films/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FilmDTO>> partialUpdateFilm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FilmDTO filmDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Film partially : {}, {}", id, filmDTO);
        if (filmDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, filmDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return filmRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FilmDTO> result = filmService.partialUpdate(filmDTO);

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
     * {@code GET  /films} : get all the films.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of films in body.
     */
    @GetMapping("/films")
    public Mono<ResponseEntity<List<FilmDTO>>> getAllFilms(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Films");
        return filmService
            .countAll()
            .zipWith(filmService.findAll(pageable).collectList())
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
     * {@code GET  /films/:id} : get the "id" film.
     *
     * @param id the id of the filmDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the filmDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/films/{id}")
    public Mono<ResponseEntity<FilmDTO>> getFilm(@PathVariable Long id) {
        log.debug("REST request to get Film : {}", id);
        Mono<FilmDTO> filmDTO = filmService.findOne(id);
        return ResponseUtil.wrapOrNotFound(filmDTO);
    }

    /**
     * {@code DELETE  /films/:id} : delete the "id" film.
     *
     * @param id the id of the filmDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/films/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteFilm(@PathVariable Long id) {
        log.debug("REST request to delete Film : {}", id);
        return filmService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
