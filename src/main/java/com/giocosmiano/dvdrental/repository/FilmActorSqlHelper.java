package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FilmActorSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));

        columns.add(Column.aliased("actor_id", table, columnPrefix + "_actor_id"));
        columns.add(Column.aliased("film_id", table, columnPrefix + "_film_id"));
        return columns;
    }
}
