package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.FilmCategory;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FilmCategory}, with proper type conversions.
 */
@Service
public class FilmCategoryRowMapper implements BiFunction<Row, String, FilmCategory> {

    private final ColumnConverter converter;

    public FilmCategoryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FilmCategory} stored in the database.
     */
    @Override
    public FilmCategory apply(Row row, String prefix) {
        FilmCategory entity = new FilmCategory();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setFilmId(converter.fromRow(row, prefix + "_film_id", Long.class));
        entity.setCategoryId(converter.fromRow(row, prefix + "_category_id", Long.class));
        return entity;
    }
}
