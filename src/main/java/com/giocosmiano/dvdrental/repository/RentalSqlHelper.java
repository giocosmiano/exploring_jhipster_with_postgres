package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RentalSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("rental_id", table, columnPrefix + "_rental_id"));
        columns.add(Column.aliased("rental_date", table, columnPrefix + "_rental_date"));
        columns.add(Column.aliased("return_date", table, columnPrefix + "_return_date"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));

        columns.add(Column.aliased("inventory_id", table, columnPrefix + "_inventory_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("staff_id", table, columnPrefix + "_staff_id"));
        return columns;
    }
}
