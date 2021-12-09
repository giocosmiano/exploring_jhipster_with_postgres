package com.giocosmiano.dvdrental.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.giocosmiano.dvdrental.domain.Rental;
import com.giocosmiano.dvdrental.repository.rowmapper.CustomerRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.InventoryRowMapper;
import com.giocosmiano.dvdrental.repository.rowmapper.RentalRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Rental entity.
 */
@SuppressWarnings("unused")
class RentalRepositoryInternalImpl implements RentalRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InventoryRowMapper inventoryMapper;
    private final CustomerRowMapper customerMapper;
    private final StaffRowMapper staffMapper;
    private final RentalRowMapper rentalMapper;

    private static final Table entityTable = Table.aliased("rental", EntityManager.ENTITY_ALIAS);
    private static final Table inventoryTable = Table.aliased("inventory", "inventory");
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table staffTable = Table.aliased("staff", "staff");

    public RentalRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InventoryRowMapper inventoryMapper,
        CustomerRowMapper customerMapper,
        StaffRowMapper staffMapper,
        RentalRowMapper rentalMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.inventoryMapper = inventoryMapper;
        this.customerMapper = customerMapper;
        this.staffMapper = staffMapper;
        this.rentalMapper = rentalMapper;
    }

    @Override
    public Flux<Rental> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Rental> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Rental> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RentalSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InventorySqlHelper.getColumns(inventoryTable, "inventory"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(StaffSqlHelper.getColumns(staffTable, "staff"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(inventoryTable)
            .on(Column.create("inventory_id", entityTable))
            .equals(Column.create("id", inventoryTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(staffTable)
            .on(Column.create("staff_id", entityTable))
            .equals(Column.create("id", staffTable));

        String select = entityManager.createSelect(selectFrom, Rental.class, pageable, criteria);
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
    public Flux<Rental> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Rental> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Rental process(Row row, RowMetadata metadata) {
        Rental entity = rentalMapper.apply(row, "e");
        entity.setInventory(inventoryMapper.apply(row, "inventory"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setStaff(staffMapper.apply(row, "staff"));
        return entity;
    }

    @Override
    public <S extends Rental> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Rental> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Rental with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Rental entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
