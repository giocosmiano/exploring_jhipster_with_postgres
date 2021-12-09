package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.ActorRepository;
import com.giocosmiano.dvdrental.service.ActorService;
import com.giocosmiano.dvdrental.service.dto.ActorDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.Actor}.
 */
@RestController
@RequestMapping("/api")
public class ActorResource {

    private final Logger log = LoggerFactory.getLogger(ActorResource.class);

    private static final String ENTITY_NAME = "actor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ActorService actorService;

    private final ActorRepository actorRepository;

    public ActorResource(ActorService actorService, ActorRepository actorRepository) {
        this.actorService = actorService;
        this.actorRepository = actorRepository;
    }

    /**
     * {@code POST  /actors} : Create a new actor.
     *
     * @param actorDTO the actorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new actorDTO, or with status {@code 400 (Bad Request)} if the actor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/actors")
    public Mono<ResponseEntity<ActorDTO>> createActor(@Valid @RequestBody ActorDTO actorDTO) throws URISyntaxException {
        log.debug("REST request to save Actor : {}", actorDTO);
        if (actorDTO.getId() != null) {
            throw new BadRequestAlertException("A new actor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return actorService
            .save(actorDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/actors/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /actors/:id} : Updates an existing actor.
     *
     * @param id the id of the actorDTO to save.
     * @param actorDTO the actorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actorDTO,
     * or with status {@code 400 (Bad Request)} if the actorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the actorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/actors/{id}")
    public Mono<ResponseEntity<ActorDTO>> updateActor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ActorDTO actorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Actor : {}, {}", id, actorDTO);
        if (actorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return actorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return actorService
                    .save(actorDTO)
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
     * {@code PATCH  /actors/:id} : Partial updates given fields of an existing actor, field will ignore if it is null
     *
     * @param id the id of the actorDTO to save.
     * @param actorDTO the actorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated actorDTO,
     * or with status {@code 400 (Bad Request)} if the actorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the actorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the actorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/actors/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ActorDTO>> partialUpdateActor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ActorDTO actorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Actor partially : {}, {}", id, actorDTO);
        if (actorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, actorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return actorRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ActorDTO> result = actorService.partialUpdate(actorDTO);

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
     * {@code GET  /actors} : get all the actors.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of actors in body.
     */
    @GetMapping("/actors")
    public Mono<ResponseEntity<List<ActorDTO>>> getAllActors(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Actors");
        return actorService
            .countAll()
            .zipWith(actorService.findAll(pageable).collectList())
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
     * {@code GET  /actors/:id} : get the "id" actor.
     *
     * @param id the id of the actorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the actorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/actors/{id}")
    public Mono<ResponseEntity<ActorDTO>> getActor(@PathVariable Long id) {
        log.debug("REST request to get Actor : {}", id);
        Mono<ActorDTO> actorDTO = actorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(actorDTO);
    }

    /**
     * {@code DELETE  /actors/:id} : delete the "id" actor.
     *
     * @param id the id of the actorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/actors/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteActor(@PathVariable Long id) {
        log.debug("REST request to delete Actor : {}", id);
        return actorService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
