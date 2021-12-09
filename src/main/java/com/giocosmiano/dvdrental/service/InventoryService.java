package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Inventory;
import com.giocosmiano.dvdrental.repository.InventoryRepository;
import com.giocosmiano.dvdrental.service.dto.InventoryDTO;
import com.giocosmiano.dvdrental.service.mapper.InventoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Inventory}.
 */
@Service
@Transactional
public class InventoryService {

    private final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    /**
     * Save a inventory.
     *
     * @param inventoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InventoryDTO> save(InventoryDTO inventoryDTO) {
        log.debug("Request to save Inventory : {}", inventoryDTO);
        return inventoryRepository.save(inventoryMapper.toEntity(inventoryDTO)).map(inventoryMapper::toDto);
    }

    /**
     * Partially update a inventory.
     *
     * @param inventoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<InventoryDTO> partialUpdate(InventoryDTO inventoryDTO) {
        log.debug("Request to partially update Inventory : {}", inventoryDTO);

        return inventoryRepository
            .findById(inventoryDTO.getId())
            .map(existingInventory -> {
                inventoryMapper.partialUpdate(existingInventory, inventoryDTO);

                return existingInventory;
            })
            .flatMap(inventoryRepository::save)
            .map(inventoryMapper::toDto);
    }

    /**
     * Get all the inventories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InventoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Inventories");
        return inventoryRepository.findAllBy(pageable).map(inventoryMapper::toDto);
    }

    /**
     * Returns the number of inventories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return inventoryRepository.count();
    }

    /**
     * Get one inventory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<InventoryDTO> findOne(Long id) {
        log.debug("Request to get Inventory : {}", id);
        return inventoryRepository.findById(id).map(inventoryMapper::toDto);
    }

    /**
     * Delete the inventory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Inventory : {}", id);
        return inventoryRepository.deleteById(id);
    }
}
