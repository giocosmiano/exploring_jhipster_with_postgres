package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.repository.FilmRepository;
import com.giocosmiano.dvdrental.service.dto.FilmDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Film}.
 */
@Service
@Transactional
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmRepository filmRepository;

    private final FilmMapper filmMapper;

    public FilmService(FilmRepository filmRepository, FilmMapper filmMapper) {
        this.filmRepository = filmRepository;
        this.filmMapper = filmMapper;
    }

    /**
     * Save a film.
     *
     * @param filmDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FilmDTO> save(FilmDTO filmDTO) {
        log.debug("Request to save Film : {}", filmDTO);
        return filmRepository.save(filmMapper.toEntity(filmDTO)).map(filmMapper::toDto);
    }

    /**
     * Partially update a film.
     *
     * @param filmDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FilmDTO> partialUpdate(FilmDTO filmDTO) {
        log.debug("Request to partially update Film : {}", filmDTO);

        return filmRepository
            .findById(filmDTO.getId())
            .map(existingFilm -> {
                filmMapper.partialUpdate(existingFilm, filmDTO);

                return existingFilm;
            })
            .flatMap(filmRepository::save)
            .map(filmMapper::toDto);
    }

    /**
     * Get all the films.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FilmDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Films");
        return filmRepository.findAllBy(pageable).map(filmMapper::toDto);
    }

    /**
     * Returns the number of films available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return filmRepository.count();
    }

    /**
     * Get one film by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FilmDTO> findOne(Long id) {
        log.debug("Request to get Film : {}", id);
        return filmRepository.findById(id).map(filmMapper::toDto);
    }

    /**
     * Delete the film by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Film : {}", id);
        return filmRepository.deleteById(id);
    }
}
