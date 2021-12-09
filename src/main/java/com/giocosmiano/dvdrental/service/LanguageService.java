package com.giocosmiano.dvdrental.service;

import com.giocosmiano.dvdrental.domain.Language;
import com.giocosmiano.dvdrental.repository.LanguageRepository;
import com.giocosmiano.dvdrental.service.dto.LanguageDTO;
import com.giocosmiano.dvdrental.service.mapper.LanguageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Language}.
 */
@Service
@Transactional
public class LanguageService {

    private final Logger log = LoggerFactory.getLogger(LanguageService.class);

    private final LanguageRepository languageRepository;

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageRepository languageRepository, LanguageMapper languageMapper) {
        this.languageRepository = languageRepository;
        this.languageMapper = languageMapper;
    }

    /**
     * Save a language.
     *
     * @param languageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<LanguageDTO> save(LanguageDTO languageDTO) {
        log.debug("Request to save Language : {}", languageDTO);
        return languageRepository.save(languageMapper.toEntity(languageDTO)).map(languageMapper::toDto);
    }

    /**
     * Partially update a language.
     *
     * @param languageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<LanguageDTO> partialUpdate(LanguageDTO languageDTO) {
        log.debug("Request to partially update Language : {}", languageDTO);

        return languageRepository
            .findById(languageDTO.getId())
            .map(existingLanguage -> {
                languageMapper.partialUpdate(existingLanguage, languageDTO);

                return existingLanguage;
            })
            .flatMap(languageRepository::save)
            .map(languageMapper::toDto);
    }

    /**
     * Get all the languages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<LanguageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Languages");
        return languageRepository.findAllBy(pageable).map(languageMapper::toDto);
    }

    /**
     * Returns the number of languages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return languageRepository.count();
    }

    /**
     * Get one language by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<LanguageDTO> findOne(Long id) {
        log.debug("Request to get Language : {}", id);
        return languageRepository.findById(id).map(languageMapper::toDto);
    }

    /**
     * Delete the language by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Language : {}", id);
        return languageRepository.deleteById(id);
    }
}
