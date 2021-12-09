package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.FilmCategory;
import com.giocosmiano.dvdrental.repository.FilmCategoryRepository;
import com.giocosmiano.dvdrental.service.dto.FilmCategoryDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link FilmCategory}.
 */
@Service
@Transactional
public class FilmCategoryService {

    private final Logger log = LoggerFactory.getLogger(FilmCategoryService.class);

    private final FilmCategoryRepository filmCategoryRepository;

    private final FilmCategoryMapper filmCategoryMapper;

    public FilmCategoryService(FilmCategoryRepository filmCategoryRepository, FilmCategoryMapper filmCategoryMapper) {
        this.filmCategoryRepository = filmCategoryRepository;
        this.filmCategoryMapper = filmCategoryMapper;
    }

    /**
     * Save a filmCategory.
     *
     * @param filmCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FilmCategoryDTO> save(FilmCategoryDTO filmCategoryDTO) {
        log.debug("Request to save FilmCategory : {}", filmCategoryDTO);
        return filmCategoryRepository.save(filmCategoryMapper.toEntity(filmCategoryDTO)).map(filmCategoryMapper::toDto);
    }

    /**
     * Partially update a filmCategory.
     *
     * @param filmCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FilmCategoryDTO> partialUpdate(FilmCategoryDTO filmCategoryDTO) {
        log.debug("Request to partially update FilmCategory : {}", filmCategoryDTO);

        return filmCategoryRepository
            .findById(filmCategoryDTO.getId())
            .map(existingFilmCategory -> {
                filmCategoryMapper.partialUpdate(existingFilmCategory, filmCategoryDTO);

                return existingFilmCategory;
            })
            .flatMap(filmCategoryRepository::save)
            .map(filmCategoryMapper::toDto);
    }

    /**
     * Get all the filmCategories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FilmCategoryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all FilmCategories");
        return filmCategoryRepository.findAllBy(pageable).map(filmCategoryMapper::toDto);
    }

    /**
     * Returns the number of filmCategories available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return filmCategoryRepository.count();
    }

    /**
     * Get one filmCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FilmCategoryDTO> findOne(Long id) {
        log.debug("Request to get FilmCategory : {}", id);
        return filmCategoryRepository.findById(id).map(filmCategoryMapper::toDto);
    }

    /**
     * Delete the filmCategory by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete FilmCategory : {}", id);
        return filmCategoryRepository.deleteById(id);
    }
}
