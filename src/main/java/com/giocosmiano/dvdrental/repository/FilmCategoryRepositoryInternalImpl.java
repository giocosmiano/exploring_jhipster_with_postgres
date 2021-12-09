package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.FilmCategory;
import com.giocosmiano.dvdrental.repository.rowmapper.CategoryRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.FilmCategoryRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.FilmRowMapper;
import com.giocosmiano.dvdrental.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the FilmCategory entity.
 */
@SuppressWarnings("unused")
class FilmCategoryRepositoryInternalImpl implements FilmCategoryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FilmRowMapper filmMapper;
    private final CategoryRowMapper categoryMapper;
    private final FilmCategoryRowMapper filmcategoryMapper;

    private static final Table entityTable = Table.aliased("film_category", EntityManager.ENTITY_ALIAS);
    private static final Table filmTable = Table.aliased("film", "film");
    private static final Table categoryTable = Table.aliased("category", "category");

    public FilmCategoryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FilmRowMapper filmMapper,
        CategoryRowMapper categoryMapper,
        FilmCategoryRowMapper filmcategoryMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.filmMapper = filmMapper;
        this.categoryMapper = categoryMapper;
        this.filmcategoryMapper = filmcategoryMapper;
    }

    @Override
    public Flux<FilmCategory> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<FilmCategory> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<FilmCategory> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = FilmCategorySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(FilmSqlHelper.getColumns(filmTable, "film"));
        columns.addAll(CategorySqlHelper.getColumns(categoryTable, "category"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(filmTable)
            .on(Column.create("film_id", entityTable))
            .equals(Column.create("id", filmTable))
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable));

        String select = entityManager.createSelect(selectFrom, FilmCategory.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<FilmCategory> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<FilmCategory> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private FilmCategory process(Row row, RowMetadata metadata) {
        FilmCategory entity = filmcategoryMapper.apply(row, "e");
        entity.setFilm(filmMapper.apply(row, "film"));
        entity.setCategory(categoryMapper.apply(row, "category"));
        return entity;
    }

    @Override
    public <S extends FilmCategory> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends FilmCategory> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update FilmCategory with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(FilmCategory entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
