package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Inventory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InventoryRepository extends R2dbcRepository<Inventory, Long>, InventoryRepositoryInternal {
    Flux<Inventory> findAllBy(Pageable pageable);

    @Query("SELECT * FROM inventory entity WHERE entity.film_id = :id")
    Flux<Inventory> findByFilm(Long id);

    @Query("SELECT * FROM inventory entity WHERE entity.film_id IS NULL")
    Flux<Inventory> findAllWhereFilmIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Inventory> findAll();

    @Override
    Mono<Inventory> findById(Long id);

    @Override
    <S extends Inventory> Mono<S> save(S entity);
}

interface InventoryRepositoryInternal {
    <S extends Inventory> Mono<S> insert(S entity);
    <S extends Inventory> Mono<S> save(S entity);
    Mono<Integer> update(Inventory entity);

    Flux<Inventory> findAll();
    Mono<Inventory> findById(Long id);
    Flux<Inventory> findAllBy(Pageable pageable);
    Flux<Inventory> findAllBy(Pageable pageable, Criteria criteria);
}
