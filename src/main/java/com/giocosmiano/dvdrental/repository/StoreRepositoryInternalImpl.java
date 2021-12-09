package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.Store;
import com.giocosmiano.dvdrental.repository.rowmapper.AddressRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.StaffRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.StoreRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Store entity.
 */
@SuppressWarnings("unused")
class StoreRepositoryInternalImpl implements StoreRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final StaffRowMapper staffMapper;
    private final AddressRowMapper addressMapper;
    private final StoreRowMapper storeMapper;

    private static final Table entityTable = Table.aliased("store", EntityManager.ENTITY_ALIAS);
    private static final Table managerStaffTable = Table.aliased("staff", "managerStaff");
    private static final Table addressTable = Table.aliased("address", "address");

    public StoreRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        StaffRowMapper staffMapper,
        AddressRowMapper addressMapper,
        StoreRowMapper storeMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.staffMapper = staffMapper;
        this.addressMapper = addressMapper;
        this.storeMapper = storeMapper;
    }

    @Override
    public Flux<Store> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Store> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Store> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = StoreSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(StaffSqlHelper.getColumns(managerStaffTable, "managerStaff"));
        columns.addAll(AddressSqlHelper.getColumns(addressTable, "address"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(managerStaffTable)
            .on(Column.create("manager_staff_id", entityTable))
            .equals(Column.create("id", managerStaffTable))
            .leftOuterJoin(addressTable)
            .on(Column.create("address_id", entityTable))
            .equals(Column.create("id", addressTable));

        String select = entityManager.createSelect(selectFrom, Store.class, pageable, criteria);
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
    public Flux<Store> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Store> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Store process(Row row, RowMetadata metadata) {
        Store entity = storeMapper.apply(row, "e");
        entity.setManagerStaff(staffMapper.apply(row, "managerStaff"));
        entity.setAddress(addressMapper.apply(row, "address"));
        return entity;
    }

    @Override
    public <S extends Store> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Store> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Store with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Store entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
