package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.repository.rowmapper.FilmRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.LanguageRowMapper;
import com.giocosmiano.dvdrental.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.math.BigDecimal;
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
 * Spring Data SQL reactive custom repository implementation for the Film entity.
 */
@SuppressWarnings("unused")
class FilmRepositoryInternalImpl implements FilmRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LanguageRowMapper languageMapper;
    private final FilmRowMapper filmMapper;

    private static final Table entityTable = Table.aliased("film", EntityManager.ENTITY_ALIAS);
    private static final Table languageTable = Table.aliased("language", "language");

    public FilmRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        LanguageRowMapper languageMapper,
        FilmRowMapper filmMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.languageMapper = languageMapper;
        this.filmMapper = filmMapper;
    }

    @Override
    public Flux<Film> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Film> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Film> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = FilmSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(LanguageSqlHelper.getColumns(languageTable, "language"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(languageTable)
            .on(Column.create("language_id", entityTable))
            .equals(Column.create("id", languageTable));

        String select = entityManager.createSelect(selectFrom, Film.class, pageable, criteria);
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
    public Flux<Film> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Film> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Film process(Row row, RowMetadata metadata) {
        Film entity = filmMapper.apply(row, "e");
        entity.setLanguage(languageMapper.apply(row, "language"));
        return entity;
    }

    @Override
    public <S extends Film> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Film> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Film with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Film entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
