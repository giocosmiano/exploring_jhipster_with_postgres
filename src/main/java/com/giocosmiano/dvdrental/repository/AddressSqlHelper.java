package com.giocosmiano.dvdrental.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AddressSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("address_id", table, columnPrefix + "_address_id"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));
        columns.add(Column.aliased("address_2", table, columnPrefix + "_address_2"));
        columns.add(Column.aliased("district", table, columnPrefix + "_district"));
        columns.add(Column.aliased("postal_code", table, columnPrefix + "_postal_code"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("last_update", table, columnPrefix + "_last_update"));

        columns.add(Column.aliased("city_id", table, columnPrefix + "_city_id"));
        return columns;
    }
}
