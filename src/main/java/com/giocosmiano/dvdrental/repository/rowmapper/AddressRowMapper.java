package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Address;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Address}, with proper type conversions.
 */
@Service
public class AddressRowMapper implements BiFunction<Row, String, Address> {

    private final ColumnConverter converter;

    public AddressRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Address} stored in the database.
     */
    @Override
    public Address apply(Row row, String prefix) {
        Address entity = new Address();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAddressId(converter.fromRow(row, prefix + "_address_id", Integer.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setAddress2(converter.fromRow(row, prefix + "_address_2", String.class));
        entity.setDistrict(converter.fromRow(row, prefix + "_district", String.class));
        entity.setPostalCode(converter.fromRow(row, prefix + "_postal_code", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Long.class));
        return entity;
    }
}
