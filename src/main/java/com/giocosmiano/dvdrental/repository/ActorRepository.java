package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Actor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Actor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActorRepository extends R2dbcRepository<Actor, Long>, ActorRepositoryInternal {
    Flux<Actor> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<Actor> findAll();

    @Override
    Mono<Actor> findById(Long id);

    @Override
    <S extends Actor> Mono<S> save(S entity);
}

interface ActorRepositoryInternal {
    <S extends Actor> Mono<S> insert(S entity);
    <S extends Actor> Mono<S> save(S entity);
    Mono<Integer> update(Actor entity);

    Flux<Actor> findAll();
    Mono<Actor> findById(Long id);
    Flux<Actor> findAllBy(Pageable pageable);
    Flux<Actor> findAllBy(Pageable pageable, Criteria criteria);
}
