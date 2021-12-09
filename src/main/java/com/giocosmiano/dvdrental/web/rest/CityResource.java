package com.giocosmiano.dvdrental.web.rest;

import com.giocosmiano.dvdrental.repository.CityRepository;
import com.giocosmiano.dvdrental.service.CityService;
import com.giocosmiano.dvdrental.service.dto.CityDTO;
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
 * REST controller for managing {@link com.giocosmiano.dvdrental.domain.City}.
 */
@RestController
@RequestMapping("/api")
public class CityResource {

    private final Logger log = LoggerFactory.getLogger(CityResource.class);

    private static final String ENTITY_NAME = "city";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CityService cityService;

    private final CityRepository cityRepository;

    public CityResource(CityService cityService, CityRepository cityRepository) {
        this.cityService = cityService;
        this.cityRepository = cityRepository;
    }

    /**
     * {@code POST  /cities} : Create a new city.
     *
     * @param cityDTO the cityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cityDTO, or with status {@code 400 (Bad Request)} if the city has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cities")
    public Mono<ResponseEntity<CityDTO>> createCity(@Valid @RequestBody CityDTO cityDTO) throws URISyntaxException {
        log.debug("REST request to save City : {}", cityDTO);
        if (cityDTO.getId() != null) {
            throw new BadRequestAlertException("A new city cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cityService
            .save(cityDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/cities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /cities/:id} : Updates an existing city.
     *
     * @param id the id of the cityDTO to save.
     * @param cityDTO the cityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityDTO,
     * or with status {@code 400 (Bad Request)} if the cityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cities/{id}")
    public Mono<ResponseEntity<CityDTO>> updateCity(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CityDTO cityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update City : {}, {}", id, cityDTO);
        if (cityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return cityService
                    .save(cityDTO)
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
     * {@code PATCH  /cities/:id} : Partial updates given fields of an existing city, field will ignore if it is null
     *
     * @param id the id of the cityDTO to save.
     * @param cityDTO the cityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cityDTO,
     * or with status {@code 400 (Bad Request)} if the cityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CityDTO>> partialUpdateCity(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CityDTO cityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update City partially : {}, {}", id, cityDTO);
        if (cityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CityDTO> result = cityService.partialUpdate(cityDTO);

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
     * {@code GET  /cities} : get all the cities.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cities in body.
     */
    @GetMapping("/cities")
    public Mono<ResponseEntity<List<CityDTO>>> getAllCities(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Cities");
        return cityService
            .countAll()
            .zipWith(cityService.findAll(pageable).collectList())
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
     * {@code GET  /cities/:id} : get the "id" city.
     *
     * @param id the id of the cityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cities/{id}")
    public Mono<ResponseEntity<CityDTO>> getCity(@PathVariable Long id) {
        log.debug("REST request to get City : {}", id);
        Mono<CityDTO> cityDTO = cityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cityDTO);
    }

    /**
     * {@code DELETE  /cities/:id} : delete the "id" city.
     *
     * @param id the id of the cityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cities/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCity(@PathVariable Long id) {
        log.debug("REST request to delete City : {}", id);
        return cityService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
