package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.City;
import com.giocosmiano.dvdrental.repository.CityRepository;
import com.giocosmiano.dvdrental.service.dto.CityDTO;
import com.giocosmiano.dvdrental.service.mapper.CityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link City}.
 */
@Service
@Transactional
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    public CityService(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    /**
     * Save a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CityDTO> save(CityDTO cityDTO) {
        log.debug("Request to save City : {}", cityDTO);
        return cityRepository.save(cityMapper.toEntity(cityDTO)).map(cityMapper::toDto);
    }

    /**
     * Partially update a city.
     *
     * @param cityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CityDTO> partialUpdate(CityDTO cityDTO) {
        log.debug("Request to partially update City : {}", cityDTO);

        return cityRepository
            .findById(cityDTO.getId())
            .map(existingCity -> {
                cityMapper.partialUpdate(existingCity, cityDTO);

                return existingCity;
            })
            .flatMap(cityRepository::save)
            .map(cityMapper::toDto);
    }

    /**
     * Get all the cities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Cities");
        return cityRepository.findAllBy(pageable).map(cityMapper::toDto);
    }

    /**
     * Returns the number of cities available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cityRepository.count();
    }

    /**
     * Get one city by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CityDTO> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findById(id).map(cityMapper::toDto);
    }

    /**
     * Delete the city by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete City : {}", id);
        return cityRepository.deleteById(id);
    }
}
