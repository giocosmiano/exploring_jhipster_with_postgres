package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CustomerSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("store_id", table, columnPrefix + "_store_id"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("activebool", table, columnPrefix + "_activebool"));
        columns.add(Column.aliased("create_date", table, columnPrefix + "_create_date"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));
        columns.add(Column.aliased("active", table, columnPrefix + "_active"));

        columns.add(Column.aliased("address_id", table, columnPrefix + "_address_id"));
        return columns;
    }
}
