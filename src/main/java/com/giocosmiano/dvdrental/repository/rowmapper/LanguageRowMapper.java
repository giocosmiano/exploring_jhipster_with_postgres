package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Language;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Language}, with proper type conversions.
 */
@Service
public class LanguageRowMapper implements BiFunction<Row, String, Language> {

    private final ColumnConverter converter;

    public LanguageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Language} stored in the database.
     */
    @Override
    public Language apply(Row row, String prefix) {
        Language entity = new Language();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLanguageId(converter.fromRow(row, prefix + "_language_id", Integer.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        return entity;
    }
}
