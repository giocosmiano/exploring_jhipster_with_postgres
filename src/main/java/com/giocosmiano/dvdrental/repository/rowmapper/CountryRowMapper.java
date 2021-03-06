package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Country;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Country}, with proper type conversions.
 */
@Service
public class CountryRowMapper implements BiFunction<Row, String, Country> {

    private final ColumnConverter converter;

    public CountryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Country} stored in the database.
     */
    @Override
    public Country apply(Row row, String prefix) {
        Country entity = new Country();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCountryId(converter.fromRow(row, prefix + "_country_id", Integer.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        return entity;
    }
}
