package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class StaffSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("staff_id", table, columnPrefix + "_staff_id"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("store_id", table, columnPrefix + "_store_id"));
        columns.add(Column.aliased("active", table, columnPrefix + "_active"));
        columns.add(Column.aliased("username", table, columnPrefix + "_username"));
        columns.add(Column.aliased("password", table, columnPrefix + "_password"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));
        columns.add(Column.aliased("picture", table, columnPrefix + "_picture"));
        columns.add(Column.aliased("picture_content_type", table, columnPrefix + "_picture_content_type"));

        columns.add(Column.aliased("address_id", table, columnPrefix + "_address_id"));
        return columns;
    }
}
