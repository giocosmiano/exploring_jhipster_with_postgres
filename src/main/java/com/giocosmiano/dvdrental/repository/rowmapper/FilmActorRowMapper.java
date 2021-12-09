package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.FilmActor;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link FilmActor}, with proper type conversions.
 */
@Service
public class FilmActorRowMapper implements BiFunction<Row, String, FilmActor> {

    private final ColumnConverter converter;

    public FilmActorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link FilmActor} stored in the database.
     */
    @Override
    public FilmActor apply(Row row, String prefix) {
        FilmActor entity = new FilmActor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        entity.setActorId(converter.fromRow(row, prefix + "_actor_id", Long.class));
        entity.setFilmId(converter.fromRow(row, prefix + "_film_id", Long.class));
        return entity;
    }
}
