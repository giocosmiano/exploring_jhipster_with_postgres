package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Film;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Film entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilmRepository extends R2dbcRepository<Film, Long>, FilmRepositoryInternal {
    Flux<Film> findAllBy(Pageable pageable);

    @Query("SELECT * FROM film entity WHERE entity.language_id = :id")
    Flux<Film> findByLanguage(Long id);

    @Query("SELECT * FROM film entity WHERE entity.language_id IS NULL")
    Flux<Film> findAllWhereLanguageIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Film> findAll();

    @Override
    Mono<Film> findById(Long id);

    @Override
    <S extends Film> Mono<S> save(S entity);
}

interface FilmRepositoryInternal {
    <S extends Film> Mono<S> insert(S entity);
    <S extends Film> Mono<S> save(S entity);
    Mono<Integer> update(Film entity);

    Flux<Film> findAll();
    Mono<Film> findById(Long id);
    Flux<Film> findAllBy(Pageable pageable);
    Flux<Film> findAllBy(Pageable pageable, Criteria criteria);
}
