package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Inventory;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Inventory}, with proper type conversions.
 */
@Service
public class InventoryRowMapper implements BiFunction<Row, String, Inventory> {

    private final ColumnConverter converter;

    public InventoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Inventory} stored in the database.
     */
    @Override
    public Inventory apply(Row row, String prefix) {
        Inventory entity = new Inventory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setInventoryId(converter.fromRow(row, prefix + "_inventory_id", Integer.class));
        entity.setStoreId(converter.fromRow(row, prefix + "_store_id", Integer.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setFilmId(converter.fromRow(row, prefix + "_film_id", Long.class));
        return entity;
    }
}
