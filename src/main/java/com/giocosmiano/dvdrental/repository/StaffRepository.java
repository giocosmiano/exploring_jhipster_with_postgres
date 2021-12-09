package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.Staff;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Staff entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StaffRepository extends R2dbcRepository<Staff, Long>, StaffRepositoryInternal {
    Flux<Staff> findAllBy(Pageable pageable);

    @Query("SELECT * FROM staff entity WHERE entity.address_id = :id")
    Flux<Staff> findByAddress(Long id);

    @Query("SELECT * FROM staff entity WHERE entity.address_id IS NULL")
    Flux<Staff> findAllWhereAddressIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Staff> findAll();

    @Override
    Mono<Staff> findById(Long id);

    @Override
    <S extends Staff> Mono<S> save(S entity);
}

interface StaffRepositoryInternal {
    <S extends Staff> Mono<S> insert(S entity);
    <S extends Staff> Mono<S> save(S entity);
    Mono<Integer> update(Staff entity);

    Flux<Staff> findAll();
    Mono<Staff> findById(Long id);
    Flux<Staff> findAllBy(Pageable pageable);
    Flux<Staff> findAllBy(Pageable pageable, Criteria criteria);
}
