package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Actor;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Actor}, with proper type conversions.
 */
@Service
public class ActorRowMapper implements BiFunction<Row, String, Actor> {

    private final ColumnConverter converter;

    public ActorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Actor} stored in the database.
     */
    @Override
    public Actor apply(Row row, String prefix) {
        Actor entity = new Actor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setActorId(converter.fromRow(row, prefix + "_actor_id", Integer.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setLastUpdate(converter.fromRow(row, prefix + "_last_update", Instant.class));
        return entity;
    }
}
