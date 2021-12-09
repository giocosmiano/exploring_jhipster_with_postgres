package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Address;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends R2dbcRepository<Address, Long>, AddressRepositoryInternal {
    Flux<Address> findAllBy(Pageable pageable);

    @Query("SELECT * FROM address entity WHERE entity.city_id = :id")
    Flux<Address> findByCity(Long id);

    @Query("SELECT * FROM address entity WHERE entity.city_id IS NULL")
    Flux<Address> findAllWhereCityIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Address> findAll();

    @Override
    Mono<Address> findById(Long id);

    @Override
    <S extends Address> Mono<S> save(S entity);
}

interface AddressRepositoryInternal {
    <S extends Address> Mono<S> insert(S entity);
    <S extends Address> Mono<S> save(S entity);
    Mono<Integer> update(Address entity);

    Flux<Address> findAll();
    Mono<Address> findById(Long id);
    Flux<Address> findAllBy(Pageable pageable);
    Flux<Address> findAllBy(Pageable pageable, Criteria criteria);
}
