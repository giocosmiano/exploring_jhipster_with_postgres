package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Store entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoreRepository extends R2dbcRepository<Store, Long>, StoreRepositoryInternal {
    Flux<Store> findAllBy(Pageable pageable);

    @Query("SELECT * FROM store entity WHERE entity.manager_staff_id = :id")
    Flux<Store> findByManagerStaff(Long id);

    @Query("SELECT * FROM store entity WHERE entity.manager_staff_id IS NULL")
    Flux<Store> findAllWhereManagerStaffIsNull();

    @Query("SELECT * FROM store entity WHERE entity.address_id = :id")
    Flux<Store> findByAddress(Long id);

    @Query("SELECT * FROM store entity WHERE entity.address_id IS NULL")
    Flux<Store> findAllWhereAddressIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Store> findAll();

    @Override
    Mono<Store> findById(Long id);

    @Override
    <S extends Store> Mono<S> save(S entity);
}

interface StoreRepositoryInternal {
    <S extends Store> Mono<S> insert(S entity);
    <S extends Store> Mono<S> save(S entity);
    Mono<Integer> update(Store entity);

    Flux<Store> findAll();
    Mono<Store> findById(Long id);
    Flux<Store> findAllBy(Pageable pageable);
    Flux<Store> findAllBy(Pageable pageable, Criteria criteria);
}
