package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Film}, with proper type conversions.
 */
@Service
public class FilmRowMapper implements BiFunction<Row, String, Film> {

    private final ColumnConverter converter;

    public FilmRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Film} stored in the database.
     */
    @Override
    public Film apply(Row row, String prefix) {
        Film entity = new Film();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFilmId(converter.fromRow(row, prefix + "_film_id", Integer.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setReleaseYear(converter.fromRow(row, prefix + "_release_year", Integer.class));
        entity.setRentalDuration(converter.fromRow(row, prefix + "_rental_duration", Integer.class));
        entity.setRentalRate(converter.fromRow(row, prefix + "_rental_rate", BigDecimal.class));
        entity.setLength(converter.fromRow(row, prefix + "_length", Integer.class));
        entity.setReplacementCost(converter.fromRow(row, prefix + "_replacement_cost", BigDecimal.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setSpecialFeatures(converter.fromRow(row, prefix + "_special_features", String.class));
        entity.setFulltext(converter.fromRow(row, prefix + "_fulltext", String.class));
        entity.setLanguageId(converter.fromRow(row, prefix + "_language_id", Long.class));
        return entity;
    }
}
