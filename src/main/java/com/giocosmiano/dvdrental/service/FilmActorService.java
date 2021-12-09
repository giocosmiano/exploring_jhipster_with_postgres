package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.FilmActor;
import com.giocosmiano.dvdrental.repository.FilmActorRepository;
import com.giocosmiano.dvdrental.service.dto.FilmActorDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmActorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link FilmActor}.
 */
@Service
@Transactional
public class FilmActorService {

    private final Logger log = LoggerFactory.getLogger(FilmActorService.class);

    private final FilmActorRepository filmActorRepository;

    private final FilmActorMapper filmActorMapper;

    public FilmActorService(FilmActorRepository filmActorRepository, FilmActorMapper filmActorMapper) {
        this.filmActorRepository = filmActorRepository;
        this.filmActorMapper = filmActorMapper;
    }

    /**
     * Save a filmActor.
     *
     * @param filmActorDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FilmActorDTO> save(FilmActorDTO filmActorDTO) {
        log.debug("Request to save FilmActor : {}", filmActorDTO);
        return filmActorRepository.save(filmActorMapper.toEntity(filmActorDTO)).map(filmActorMapper::toDto);
    }

    /**
     * Partially update a filmActor.
     *
     * @param filmActorDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FilmActorDTO> partialUpdate(FilmActorDTO filmActorDTO) {
        log.debug("Request to partially update FilmActor : {}", filmActorDTO);

        return filmActorRepository
            .findById(filmActorDTO.getId())
            .map(existingFilmActor -> {
                filmActorMapper.partialUpdate(existingFilmActor, filmActorDTO);

                return existingFilmActor;
            })
            .flatMap(filmActorRepository::save)
            .map(filmActorMapper::toDto);
    }

    /**
     * Get all the filmActors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FilmActorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FilmActors");
        return filmActorRepository.findAllBy(pageable).map(filmActorMapper::toDto);
    }

    /**
     * Returns the number of filmActors available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return filmActorRepository.count();
    }

    /**
     * Get one filmActor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FilmActorDTO> findOne(Long id) {
        log.debug("Request to get FilmActor : {}", id);
        return filmActorRepository.findById(id).map(filmActorMapper::toDto);
    }

    /**
     * Delete the filmActor by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete FilmActor : {}", id);
        return filmActorRepository.deleteById(id);
    }
}
