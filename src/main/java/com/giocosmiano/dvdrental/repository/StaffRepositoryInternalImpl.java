package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.repository.rowmapper.AddressRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.StaffRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Staff entity.
 */
@SuppressWarnings("unused")
class StaffRepositoryInternalImpl implements StaffRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AddressRowMapper addressMapper;
    private final StaffRowMapper staffMapper;

    private static final Table entityTable = Table.aliased("staff", EntityManager.ENTITY_ALIAS);
    private static final Table addressTable = Table.aliased("address", "address");

    public StaffRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AddressRowMapper addressMapper,
        StaffRowMapper staffMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.addressMapper = addressMapper;
        this.staffMapper = staffMapper;
    }

    @Override
    public Flux<Staff> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Staff> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Staff> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = StaffSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AddressSqlHelper.getColumns(addressTable, "address"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(addressTable)
            .on(Column.create("address_id", entityTable))
            .equals(Column.create("id", addressTable));

        String select = entityManager.createSelect(selectFrom, Staff.class, pageable, criteria);
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
    public Flux<Staff> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Staff> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Staff process(Row row, RowMetadata metadata) {
        Staff entity = staffMapper.apply(row, "e");
        entity.setAddress(addressMapper.apply(row, "address"));
        return entity;
    }

    @Override
    public <S extends Staff> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Staff> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Staff with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Staff entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
