package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class InventorySqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("inventory_id", table, columnPrefix + "_inventory_id"));
        columns.add(Column.aliased("store_id", table, columnPrefix + "_store_id"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));

        columns.add(Column.aliased("film_id", table, columnPrefix + "_film_id"));
        return columns;
    }
}
