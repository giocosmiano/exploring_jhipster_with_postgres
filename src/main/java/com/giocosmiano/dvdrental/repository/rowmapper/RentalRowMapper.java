package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Rental;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Rental}, with proper type conversions.
 */
@Service
public class RentalRowMapper implements BiFunction<Row, String, Rental> {

    private final ColumnConverter converter;

    public RentalRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Rental} stored in the database.
     */
    @Override
    public Rental apply(Row row, String prefix) {
        Rental entity = new Rental();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRentalId(converter.fromRow(row, prefix + "_rental_id", Integer.class));
        entity.setRentalDate(converter.fromRow(row, prefix + "_rental_date", Instant.class));
        entity.setReturnDate(converter.fromRow(row, prefix + "_return_date", Instant.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setInventoryId(converter.fromRow(row, prefix + "_inventory_id", Long.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setStaffId(converter.fromRow(row, prefix + "_staff_id", Long.class));
        return entity;
    }
}
