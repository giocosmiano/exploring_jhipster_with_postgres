package com.giocosmiano.dvdrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Rental.
 */
@Table("rental")
public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("rental_id")
    private Integer rentalId;

    @NotNull(message = "must not be null")
    @Column("rental_date")
    private Instant rentalDate;

    @Column("return_date")
    private Instant returnDate;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "film", "rentals" }, allowSetters = true)
    private Inventory inventory;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals" }, allowSetters = true)
    private Customer customer;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals", "stores" }, allowSetters = true)
    private Staff staff;

    @Transient
    @JsonIgnoreProperties(value = { "customer", "staff", "rental" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @Column("inventory_id")
    private Long inventoryId;

    @Column("customer_id")
    private Long customerId;

    @Column("staff_id")
    private Long staffId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rental id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRentalId() {
        return this.rentalId;
    }

    public Rental rentalId(Integer rentalId) {
        this.setRentalId(rentalId);
        return this;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public Instant getRentalDate() {
        return this.rentalDate;
    }

    public Rental rentalDate(Instant rentalDate) {
        this.setRentalDate(rentalDate);
        return this;
    }

    public void setRentalDate(Instant rentalDate) {
        this.rentalDate = rentalDate;
    }

    public Instant getReturnDate() {
        return this.returnDate;
    }

    public Rental returnDate(Instant returnDate) {
        this.setReturnDate(returnDate);
        return this;
    }

    public void setReturnDate(Instant returnDate) {
        this.returnDate = returnDate;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Rental lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        this.inventoryId = inventory != null ? inventory.getId() : null;
    }

    public Rental inventory(Inventory inventory) {
        this.setInventory(inventory);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Rental customer(Customer customer) {
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

    public Rental staff(Staff staff) {
        this.setStaff(staff);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setRental(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setRental(this));
        }
        this.payments = payments;
    }

    public Rental payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Rental addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setRental(this);
        return this;
    }

    public Rental removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setRental(null);
        return this;
    }

    public Long getInventoryId() {
        return this.inventoryId;
    }

    public void setInventoryId(Long inventory) {
        this.inventoryId = inventory;
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

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rental)) {
            return false;
        }
        return id != null && id.equals(((Rental) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rental{" +
            "id=" + getId() +
            ", rentalId=" + getRentalId() +
            ", rentalDate='" + getRentalDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
