package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Store;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Store}, with proper type conversions.
 */
@Service
public class StoreRowMapper implements BiFunction<Row, String, Store> {

    private final ColumnConverter converter;

    public StoreRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Store} stored in the database.
     */
    @Override
    public Store apply(Row row, String prefix) {
        Store entity = new Store();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStoreId(converter.fromRow(row, prefix + "_store_id", Integer.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setManagerStaffId(converter.fromRow(row, prefix + "_manager_staff_id", Long.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        return entity;
    }
}
