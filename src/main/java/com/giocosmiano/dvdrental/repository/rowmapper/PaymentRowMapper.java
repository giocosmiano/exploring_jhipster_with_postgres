package com.giocosmiano.dvdrental.repository.rowmapper;

import com.giocosmiano.dvdrental.domain.Payment;
import com.giocosmiano.dvdrental.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Payment}, with proper type conversions.
 */
@Service
public class PaymentRowMapper implements BiFunction<Row, String, Payment> {

    private final ColumnConverter converter;

    public PaymentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Payment} stored in the database.
     */
    @Override
    public Payment apply(Row row, String prefix) {
        Payment entity = new Payment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPaymentId(converter.fromRow(row, prefix + "_payment_id", Integer.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", BigDecimal.class));
        entity.setPaymentDate(converter.fromRow(row, prefix + "_payment_date", Instant.class));
        entity.setCustomerId(converter.fromRow(row, prefix + "_customer_id", Long.class));
        entity.setStaffId(converter.fromRow(row, prefix + "_staff_id", Long.class));
        entity.setRentalId(converter.fromRow(row, prefix + "_rental_id", Long.class));
        return entity;
    }
}
