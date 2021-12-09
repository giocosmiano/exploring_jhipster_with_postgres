package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Country;
import com.giocosmiano.dvdrental.repository.CountryRepository;
import com.giocosmiano.dvdrental.service.dto.CountryDTO;
import com.giocosmiano.dvdrental.service.mapper.CountryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Country}.
 */
@Service
@Transactional
public class CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    public CountryService(CountryRepository countryRepository, CountryMapper countryMapper) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    /**
     * Save a country.
     *
     * @param countryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CountryDTO> save(CountryDTO countryDTO) {
        log.debug("Request to save Country : {}", countryDTO);
        return countryRepository.save(countryMapper.toEntity(countryDTO)).map(countryMapper::toDto);
    }

    /**
     * Partially update a country.
     *
     * @param countryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CountryDTO> partialUpdate(CountryDTO countryDTO) {
        log.debug("Request to partially update Country : {}", countryDTO);

        return countryRepository
            .findById(countryDTO.getId())
            .map(existingCountry -> {
                countryMapper.partialUpdate(existingCountry, countryDTO);

                return existingCountry;
            })
            .flatMap(countryRepository::save)
            .map(countryMapper::toDto);
    }

    /**
     * Get all the countries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CountryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Countries");
        return countryRepository.findAllBy(pageable).map(countryMapper::toDto);
    }

    /**
     * Returns the number of countries available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return countryRepository.count();
    }

    /**
     * Get one country by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CountryDTO> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id).map(countryMapper::toDto);
    }

    /**
     * Delete the country by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        return countryRepository.deleteById(id);
    }
}
