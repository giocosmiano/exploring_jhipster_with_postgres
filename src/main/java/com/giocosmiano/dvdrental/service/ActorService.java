package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Actor;
import com.giocosmiano.dvdrental.repository.ActorRepository;
import com.giocosmiano.dvdrental.service.dto.ActorDTO;
import com.giocosmiano.dvdrental.service.mapper.ActorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Actor}.
 */
@Service
@Transactional
public class ActorService {

    private final Logger log = LoggerFactory.getLogger(ActorService.class);

    private final ActorRepository actorRepository;

    private final ActorMapper actorMapper;

    public ActorService(ActorRepository actorRepository, ActorMapper actorMapper) {
        this.actorRepository = actorRepository;
        this.actorMapper = actorMapper;
    }

    /**
     * Save a actor.
     *
     * @param actorDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ActorDTO> save(ActorDTO actorDTO) {
        log.debug("Request to save Actor : {}", actorDTO);
        return actorRepository.save(actorMapper.toEntity(actorDTO)).map(actorMapper::toDto);
    }

    /**
     * Partially update a actor.
     *
     * @param actorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ActorDTO> partialUpdate(ActorDTO actorDTO) {
        log.debug("Request to partially update Actor : {}", actorDTO);

        return actorRepository
            .findById(actorDTO.getId())
            .map(existingActor -> {
                actorMapper.partialUpdate(existingActor, actorDTO);

                return existingActor;
            })
            .flatMap(actorRepository::save)
            .map(actorMapper::toDto);
    }

    /**
     * Get all the actors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ActorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Actors");
        return actorRepository.findAllBy(pageable).map(actorMapper::toDto);
    }

    /**
     * Returns the number of actors available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return actorRepository.count();
    }

    /**
     * Get one actor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ActorDTO> findOne(Long id) {
        log.debug("Request to get Actor : {}", id);
        return actorRepository.findById(id).map(actorMapper::toDto);
    }

    /**
     * Delete the actor by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Actor : {}", id);
        return actorRepository.deleteById(id);
    }
}
