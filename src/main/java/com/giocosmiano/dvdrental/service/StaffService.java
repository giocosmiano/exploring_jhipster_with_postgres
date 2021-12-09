package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.repository.StaffRepository;
import com.giocosmiano.dvdrental.service.dto.StaffDTO;
import com.giocosmiano.dvdrental.service.mapper.StaffMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Staff}.
 */
@Service
@Transactional
public class StaffService {

    private final Logger log = LoggerFactory.getLogger(StaffService.class);

    private final StaffRepository staffRepository;

    private final StaffMapper staffMapper;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    /**
     * Save a staff.
     *
     * @param staffDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<StaffDTO> save(StaffDTO staffDTO) {
        log.debug("Request to save Staff : {}", staffDTO);
        return staffRepository.save(staffMapper.toEntity(staffDTO)).map(staffMapper::toDto);
    }

    /**
     * Partially update a staff.
     *
     * @param staffDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<StaffDTO> partialUpdate(StaffDTO staffDTO) {
        log.debug("Request to partially update Staff : {}", staffDTO);

        return staffRepository
            .findById(staffDTO.getId())
            .map(existingStaff -> {
                staffMapper.partialUpdate(existingStaff, staffDTO);

                return existingStaff;
            })
            .flatMap(staffRepository::save)
            .map(staffMapper::toDto);
    }

    /**
     * Get all the staff.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<StaffDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Staff");
        return staffRepository.findAllBy(pageable).map(staffMapper::toDto);
    }

    /**
     * Returns the number of staff available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return staffRepository.count();
    }

    /**
     * Get one staff by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<StaffDTO> findOne(Long id) {
        log.debug("Request to get Staff : {}", id);
        return staffRepository.findById(id).map(staffMapper::toDto);
    }

    /**
     * Delete the staff by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Staff : {}", id);
        return staffRepository.deleteById(id);
    }
}
