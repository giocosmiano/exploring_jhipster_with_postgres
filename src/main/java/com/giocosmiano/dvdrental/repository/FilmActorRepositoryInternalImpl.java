package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.FilmActor;
import com.giocosmiano.dvdrental.repository.rowmapper.ActorRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.FilmActorRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the FilmActor entity.
 */
@SuppressWarnings("unused")
class FilmActorRepositoryInternalImpl implements FilmActorRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ActorRowMapper actorMapper;
    private final FilmRowMapper filmMapper;
    private final FilmActorRowMapper filmactorMapper;

    private static final Table entityTable = Table.aliased("film_actor", EntityManager.ENTITY_ALIAS);
    private static final Table actorTable = Table.aliased("actor", "actor");
    private static final Table filmTable = Table.aliased("film", "film");

    public FilmActorRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ActorRowMapper actorMapper,
        FilmRowMapper filmMapper,
        FilmActorRowMapper filmactorMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.actorMapper = actorMapper;
        this.filmMapper = filmMapper;
        this.filmactorMapper = filmactorMapper;
    }

    @Override
    public Flux<FilmActor> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<FilmActor> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<FilmActor> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = FilmActorSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ActorSqlHelper.getColumns(actorTable, "actor"));
        columns.addAll(FilmSqlHelper.getColumns(filmTable, "film"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(actorTable)
            .on(Column.create("actor_id", entityTable))
            .equals(Column.create("id", actorTable))
            .leftOuterJoin(filmTable)
            .on(Column.create("film_id", entityTable))
            .equals(Column.create("id", filmTable));

        String select = entityManager.createSelect(selectFrom, FilmActor.class, pageable, criteria);
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
    public Flux<FilmActor> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<FilmActor> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private FilmActor process(Row row, RowMetadata metadata) {
        FilmActor entity = filmactorMapper.apply(row, "e");
        entity.setActor(actorMapper.apply(row, "actor"));
        entity.setFilm(filmMapper.apply(row, "film"));
        return entity;
    }

    @Override
    public <S extends FilmActor> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends FilmActor> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update FilmActor with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(FilmActor entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
