package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.FilmActor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the FilmActor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilmActorRepository extends R2dbcRepository<FilmActor, Long>, FilmActorRepositoryInternal {
    Flux<FilmActor> findAllBy(Pageable pageable);

    @Query("SELECT * FROM film_actor entity WHERE entity.actor_id = :id")
    Flux<FilmActor> findByActor(Long id);

    @Query("SELECT * FROM film_actor entity WHERE entity.actor_id IS NULL")
    Flux<FilmActor> findAllWhereActorIsNull();

    @Query("SELECT * FROM film_actor entity WHERE entity.film_id = :id")
    Flux<FilmActor> findByFilm(Long id);

    @Query("SELECT * FROM film_actor entity WHERE entity.film_id IS NULL")
    Flux<FilmActor> findAllWhereFilmIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<FilmActor> findAll();

    @Override
    Mono<FilmActor> findById(Long id);

    @Override
    <S extends FilmActor> Mono<S> save(S entity);
}

interface FilmActorRepositoryInternal {
    <S extends FilmActor> Mono<S> insert(S entity);
    <S extends FilmActor> Mono<S> save(S entity);
    Mono<Integer> update(FilmActor entity);

    Flux<FilmActor> findAll();
    Mono<FilmActor> findById(Long id);
    Flux<FilmActor> findAllBy(Pageable pageable);
    Flux<FilmActor> findAllBy(Pageable pageable, Criteria criteria);
}
