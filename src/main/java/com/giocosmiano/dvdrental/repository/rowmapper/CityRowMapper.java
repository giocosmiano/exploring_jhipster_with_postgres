package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.City;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link City}, with proper type conversions.
 */
@Service
public class CityRowMapper implements BiFunction<Row, String, City> {

    private final ColumnConverter converter;

    public CityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link City} stored in the database.
     */
    @Override
    public City apply(Row row, String prefix) {
        City entity = new City();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCityId(converter.fromRow(row, prefix + "_city_id", Integer.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setCountryId(converter.fromRow(row, prefix + "_country_id", Long.class));
        return entity;
    }
}
