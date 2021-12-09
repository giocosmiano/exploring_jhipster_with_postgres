package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Staff}, with proper type conversions.
 */
@Service
public class StaffRowMapper implements BiFunction<Row, String, Staff> {

    private final ColumnConverter converter;

    public StaffRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Staff} stored in the database.
     */
    @Override
    public Staff apply(Row row, String prefix) {
        Staff entity = new Staff();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStaffId(converter.fromRow(row, prefix + "_staff_id", Integer.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setStoreId(converter.fromRow(row, prefix + "_store_id", Integer.class));
        entity.setActive(converter.fromRow(row, prefix + "_active", Boolean.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setPictureContentType(converter.fromRow(row, prefix + "_picture_content_type", String.class));
        entity.setPicture(converter.fromRow(row, prefix + "_picture", byte[].class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Long.class));
        return entity;
    }
}
