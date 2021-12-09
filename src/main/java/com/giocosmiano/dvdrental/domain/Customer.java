package com.giocosmiano.dvdrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Customer.
 */
@Table("customer")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("customer_id")
    private Integer customerId;

    @NotNull(message = "must not be null")
    @Column("store_id")
    private Integer storeId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("last_name")
    private String lastName;

    @Size(max = 255)
    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("activebool")
    private Boolean activebool;

    @NotNull(message = "must not be null")
    @Column("create_date")
    private LocalDate createDate;

    @Column("last_update")
    private Instant lastUpdate;

    @Column("active")
    private Integer active;

    @Transient
    @JsonIgnoreProperties(value = { "city", "customers", "staff", "stores" }, allowSetters = true)
    private Address address;

    @Transient
    @JsonIgnoreProperties(value = { "customer", "staff", "rental" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "inventory", "customer", "staff", "payments" }, allowSetters = true)
    private Set<Rental> rentals = new HashSet<>();

    @Column("address_id")
    private Long addressId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Customer customerId(Integer customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getStoreId() {
        return this.storeId;
    }

    public Customer storeId(Integer storeId) {
        this.setStoreId(storeId);
        return this;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Customer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Customer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Customer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActivebool() {
        return this.activebool;
    }

    public Customer activebool(Boolean activebool) {
        this.setActivebool(activebool);
        return this;
    }

    public void setActivebool(Boolean activebool) {
        this.activebool = activebool;
    }

    public LocalDate getCreateDate() {
        return this.createDate;
    }

    public Customer createDate(LocalDate createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Customer lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getActive() {
        return this.active;
    }

    public Customer active(Integer active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Customer address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setCustomer(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setCustomer(this));
        }
        this.payments = payments;
    }

    public Customer payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Customer addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setCustomer(this);
        return this;
    }

    public Customer removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setCustomer(null);
        return this;
    }

    public Set<Rental> getRentals() {
        return this.rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        if (this.rentals != null) {
            this.rentals.forEach(i -> i.setCustomer(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setCustomer(this));
        }
        this.rentals = rentals;
    }

    public Customer rentals(Set<Rental> rentals) {
        this.setRentals(rentals);
        return this;
    }

    public Customer addRental(Rental rental) {
        this.rentals.add(rental);
        rental.setCustomer(this);
        return this;
    }

    public Customer removeRental(Rental rental) {
        this.rentals.remove(rental);
        rental.setCustomer(null);
        return this;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setAddressId(Long address) {
        this.addressId = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", customerId=" + getCustomerId() +
            ", storeId=" + getStoreId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", activebool='" + getActivebool() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", active=" + getActive() +
            "}";
    }
}
