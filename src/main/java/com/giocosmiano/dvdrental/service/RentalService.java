package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Rental;
import com.giocosmiano.dvdrental.repository.RentalRepository;
import com.giocosmiano.dvdrental.service.dto.RentalDTO;
import com.giocosmiano.dvdrental.service.mapper.RentalMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Rental}.
 */
@Service
@Transactional
public class RentalService {

    private final Logger log = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;

    private final RentalMapper rentalMapper;

    public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.rentalMapper = rentalMapper;
    }

    /**
     * Save a rental.
     *
     * @param rentalDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> save(RentalDTO rentalDTO) {
        log.debug("Request to save Rental : {}", rentalDTO);
        return rentalRepository.save(rentalMapper.toEntity(rentalDTO)).map(rentalMapper::toDto);
    }

    /**
     * Partially update a rental.
     *
     * @param rentalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RentalDTO> partialUpdate(RentalDTO rentalDTO) {
        log.debug("Request to partially update Rental : {}", rentalDTO);

        return rentalRepository
            .findById(rentalDTO.getId())
            .map(existingRental -> {
                rentalMapper.partialUpdate(existingRental, rentalDTO);

                return existingRental;
            })
            .flatMap(rentalRepository::save)
            .map(rentalMapper::toDto);
    }

    /**
     * Get all the rentals.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RentalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Rentals");
        return rentalRepository.findAllBy(pageable).map(rentalMapper::toDto);
    }

    /**
     * Returns the number of rentals available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return rentalRepository.count();
    }

    /**
     * Get one rental by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RentalDTO> findOne(Long id) {
        log.debug("Request to get Rental : {}", id);
        return rentalRepository.findById(id).map(rentalMapper::toDto);
    }

    /**
     * Delete the rental by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Rental : {}", id);
        return rentalRepository.deleteById(id);
    }
}
