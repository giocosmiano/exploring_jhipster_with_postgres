package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class FilmSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("film_id", table, columnPrefix + "_film_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("release_year", table, columnPrefix + "_release_year"));
        columns.add(Column.aliased("rental_duration", table, columnPrefix + "_rental_duration"));
        columns.add(Column.aliased("rental_rate", table, columnPrefix + "_rental_rate"));
        columns.add(Column.aliased("length", table, columnPrefix + "_length"));
        columns.add(Column.aliased("replacement_cost", table, columnPrefix + "_replacement_cost"));
        columns.add(Column.aliased("rating", table, columnPrefix + "_rating"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));
        columns.add(Column.aliased("special_features", table, columnPrefix + "_special_features"));
        columns.add(Column.aliased("fulltext", table, columnPrefix + "_fulltext"));

        columns.add(Column.aliased("language_id", table, columnPrefix + "_language_id"));
        return columns;
    }
}
