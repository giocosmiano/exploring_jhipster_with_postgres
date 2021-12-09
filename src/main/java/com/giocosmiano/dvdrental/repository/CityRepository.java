package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.City;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the City entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityRepository extends R2dbcRepository<City, Long>, CityRepositoryInternal {
    Flux<City> findAllBy(Pageable pageable);

    @Query("SELECT * FROM city entity WHERE entity.country_id = :id")
    Flux<City> findByCountry(Long id);

    @Query("SELECT * FROM city entity WHERE entity.country_id IS NULL")
    Flux<City> findAllWhereCountryIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<City> findAll();

    @Override
    Mono<City> findById(Long id);

    @Override
    <S extends City> Mono<S> save(S entity);
}

interface CityRepositoryInternal {
    <S extends City> Mono<S> insert(S entity);
    <S extends City> Mono<S> save(S entity);
    Mono<Integer> update(City entity);

    Flux<City> findAll();
    Mono<City> findById(Long id);
    Flux<City> findAllBy(Pageable pageable);
    Flux<City> findAllBy(Pageable pageable, Criteria criteria);
}
