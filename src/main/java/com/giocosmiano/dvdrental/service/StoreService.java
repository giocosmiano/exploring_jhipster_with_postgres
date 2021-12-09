package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Store;
import com.giocosmiano.dvdrental.repository.StoreRepository;
import com.giocosmiano.dvdrental.service.dto.StoreDTO;
import com.giocosmiano.dvdrental.service.mapper.StoreMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Store}.
 */
@Service
@Transactional
public class StoreService {

    private final Logger log = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    public StoreService(StoreRepository storeRepository, StoreMapper storeMapper) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
    }

    /**
     * Save a store.
     *
     * @param storeDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<StoreDTO> save(StoreDTO storeDTO) {
        log.debug("Request to save Store : {}", storeDTO);
        return storeRepository.save(storeMapper.toEntity(storeDTO)).map(storeMapper::toDto);
    }

    /**
     * Partially update a store.
     *
     * @param storeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<StoreDTO> partialUpdate(StoreDTO storeDTO) {
        log.debug("Request to partially update Store : {}", storeDTO);

        return storeRepository
            .findById(storeDTO.getId())
            .map(existingStore -> {
                storeMapper.partialUpdate(existingStore, storeDTO);

                return existingStore;
            })
            .flatMap(storeRepository::save)
            .map(storeMapper::toDto);
    }

    /**
     * Get all the stores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<StoreDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Stores");
        return storeRepository.findAllBy(pageable).map(storeMapper::toDto);
    }

    /**
     * Returns the number of stores available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return storeRepository.count();
    }

    /**
     * Get one store by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<StoreDTO> findOne(Long id) {
        log.debug("Request to get Store : {}", id);
        return storeRepository.findById(id).map(storeMapper::toDto);
    }

    /**
     * Delete the store by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Store : {}", id);
        return storeRepository.deleteById(id);
    }
}
