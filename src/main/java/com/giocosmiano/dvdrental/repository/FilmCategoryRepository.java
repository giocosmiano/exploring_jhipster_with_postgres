package com.giocosmiano.dvdrental.repository;

import com.giocosmiano.dvdrental.domain.FilmCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the FilmCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FilmCategoryRepository extends R2dbcRepository<FilmCategory, Long>, FilmCategoryRepositoryInternal {
    Flux<FilmCategory> findAllBy(Pageable pageable);

    @Query("SELECT * FROM film_category entity WHERE entity.film_id = :id")
    Flux<FilmCategory> findByFilm(Long id);

    @Query("SELECT * FROM film_category entity WHERE entity.film_id IS NULL")
    Flux<FilmCategory> findAllWhereFilmIsNull();

    @Query("SELECT * FROM film_category entity WHERE entity.category_id = :id")
    Flux<FilmCategory> findByCategory(Long id);

    @Query("SELECT * FROM film_category entity WHERE entity.category_id IS NULL")
    Flux<FilmCategory> findAllWhereCategoryIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<FilmCategory> findAll();

    @Override
    Mono<FilmCategory> findById(Long id);

    @Override
    <S extends FilmCategory> Mono<S> save(S entity);
}

interface FilmCategoryRepositoryInternal {
    <S extends FilmCategory> Mono<S> insert(S entity);
    <S extends FilmCategory> Mono<S> save(S entity);
    Mono<Integer> update(FilmCategory entity);

    Flux<FilmCategory> findAll();
    Mono<FilmCategory> findById(Long id);
    Flux<FilmCategory> findAllBy(Pageable pageable);
    Flux<FilmCategory> findAllBy(Pageable pageable, Criteria criteria);
}
