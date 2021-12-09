package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Language;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Language entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LanguageRepository extends R2dbcRepository<Language, Long>, LanguageRepositoryInternal {
    Flux<Language> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Language> findAll();

    @Override
    Mono<Language> findById(Long id);

    @Override
    <S extends Language> Mono<S> save(S entity);
}

interface LanguageRepositoryInternal {
    <S extends Language> Mono<S> insert(S entity);
    <S extends Language> Mono<S> save(S entity);
    Mono<Integer> update(Language entity);

    Flux<Language> findAll();
    Mono<Language> findById(Long id);
    Flux<Language> findAllBy(Pageable pageable);
    Flux<Language> findAllBy(Pageable pageable, Criteria criteria);
}
