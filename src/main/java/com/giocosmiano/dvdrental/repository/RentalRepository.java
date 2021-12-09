package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Rental entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RentalRepository extends R2dbcRepository<Rental, Long>, RentalRepositoryInternal {
    Flux<Rental> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rental entity WHERE entity.inventory_id = :id")
    Flux<Rental> findByInventory(Long id);

    @Query("SELECT * FROM rental entity WHERE entity.inventory_id IS NULL")
    Flux<Rental> findAllWhereInventoryIsNull();

    @Query("SELECT * FROM rental entity WHERE entity.customer_id = :id")
    Flux<Rental> findByCustomer(Long id);

    @Query("SELECT * FROM rental entity WHERE entity.customer_id IS NULL")
    Flux<Rental> findAllWhereCustomerIsNull();

    @Query("SELECT * FROM rental entity WHERE entity.staff_id = :id")
    Flux<Rental> findByStaff(Long id);

    @Query("SELECT * FROM rental entity WHERE entity.staff_id IS NULL")
    Flux<Rental> findAllWhereStaffIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Rental> findAll();

    @Override
    Mono<Rental> findById(Long id);

    @Override
    <S extends Rental> Mono<S> save(S entity);
}

interface RentalRepositoryInternal {
    <S extends Rental> Mono<S> insert(S entity);
    <S extends Rental> Mono<S> save(S entity);
    Mono<Integer> update(Rental entity);

    Flux<Rental> findAll();
    Mono<Rental> findById(Long id);
    Flux<Rental> findAllBy(Pageable pageable);
    Flux<Rental> findAllBy(Pageable pageable, Criteria criteria);
}
