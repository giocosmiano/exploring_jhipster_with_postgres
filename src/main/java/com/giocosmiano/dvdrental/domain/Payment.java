package com.giocosmiano.dvdrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Payment.
 */
@Table("payment")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("payment_id")
    private Integer paymentId;

    @NotNull(message = "must not be null")
    @Column("amount")
    private BigDecimal amount;

    @NotNull(message = "must not be null")
    @Column("payment_date")
    private Instant paymentDate;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals" }, allowSetters = true)
    private Customer customer;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals", "stores" }, allowSetters = true)
    private Staff staff;

    @Transient
    @JsonIgnoreProperties(value = { "inventory", "customer", "staff", "payments" }, allowSetters = true)
    private Rental rental;

    @Column("customer_id")
    private Long customerId;

    @Column("staff_id")
    private Long staffId;

    @Column("rental_id")
    private Long rentalId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPaymentId() {
        return this.paymentId;
    }

    public Payment paymentId(Integer paymentId) {
        this.setPaymentId(paymentId);
        return this;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount != null ? amount.stripTrailingZeros() : null;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public Payment paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Payment customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Staff getStaff() {
        return this.staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
        this.staffId = staff != null ? staff.getId() : null;
    }

    public Payment staff(Staff staff) {
        this.setStaff(staff);
        return this;
    }

    public Rental getRental() {
        return this.rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
        this.rentalId = rental != null ? rental.getId() : null;
    }

    public Payment rental(Rental rental) {
        this.setRental(rental);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    public Long getStaffId() {
        return this.staffId;
    }

    public void setStaffId(Long staff) {
        this.staffId = staff;
    }

    public Long getRentalId() {
        return this.rentalId;
    }

    public void setRentalId(Long rental) {
        this.rentalId = rental;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", paymentId=" + getPaymentId() +
            ", amount=" + getAmount() +
            ", paymentDate='" + getPaymentDate() + "'" +
            "}";
    }
}
