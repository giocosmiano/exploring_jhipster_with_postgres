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
 * A Staff.
 */
@Table("staff")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("staff_id")
    private Integer staffId;

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
    @Column("store_id")
    private Integer storeId;

    @NotNull(message = "must not be null")
    @Column("active")
    private Boolean active;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("username")
    private String username;

    @Size(max = 255)
    @Column("password")
    private String password;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Column("picture")
    private byte[] picture;

    @Column("picture_content_type")
    private String pictureContentType;

    @Transient
    @JsonIgnoreProperties(value = { "city", "customers", "staff", "stores" }, allowSetters = true)
    private Address address;

    @Transient
    @JsonIgnoreProperties(value = { "customer", "staff", "rental" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "inventory", "customer", "staff", "payments" }, allowSetters = true)
    private Set<Rental> rentals = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "managerStaff", "address" }, allowSetters = true)
    private Set<Store> stores = new HashSet<>();

    @Column("address_id")
    private Long addressId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Staff id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStaffId() {
        return this.staffId;
    }

    public Staff staffId(Integer staffId) {
        this.setStaffId(staffId);
        return this;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Staff firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Staff lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public Staff email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStoreId() {
        return this.storeId;
    }

    public Staff storeId(Integer storeId) {
        this.setStoreId(storeId);
        return this;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Staff active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return this.username;
    }

    public Staff username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public Staff password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Staff lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public byte[] getPicture() {
        return this.picture;
    }

    public Staff picture(byte[] picture) {
        this.setPicture(picture);
        return this;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return this.pictureContentType;
    }

    public Staff pictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
        return this;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Staff address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setStaff(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setStaff(this));
        }
        this.payments = payments;
    }

    public Staff payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Staff addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setStaff(this);
        return this;
    }

    public Staff removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setStaff(null);
        return this;
    }

    public Set<Rental> getRentals() {
        return this.rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        if (this.rentals != null) {
            this.rentals.forEach(i -> i.setStaff(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setStaff(this));
        }
        this.rentals = rentals;
    }

    public Staff rentals(Set<Rental> rentals) {
        this.setRentals(rentals);
        return this;
    }

    public Staff addRental(Rental rental) {
        this.rentals.add(rental);
        rental.setStaff(this);
        return this;
    }

    public Staff removeRental(Rental rental) {
        this.rentals.remove(rental);
        rental.setStaff(null);
        return this;
    }

    public Set<Store> getStores() {
        return this.stores;
    }

    public void setStores(Set<Store> stores) {
        if (this.stores != null) {
            this.stores.forEach(i -> i.setManagerStaff(null));
        }
        if (stores != null) {
            stores.forEach(i -> i.setManagerStaff(this));
        }
        this.stores = stores;
    }

    public Staff stores(Set<Store> stores) {
        this.setStores(stores);
        return this;
    }

    public Staff addStore(Store store) {
        this.stores.add(store);
        store.setManagerStaff(this);
        return this;
    }

    public Staff removeStore(Store store) {
        this.stores.remove(store);
        store.setManagerStaff(null);
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
        if (!(o instanceof Staff)) {
            return false;
        }
        return id != null && id.equals(((Staff) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Staff{" +
            "id=" + getId() +
            ", staffId=" + getStaffId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", storeId=" + getStoreId() +
            ", active='" + getActive() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", picture='" + getPicture() + "'" +
            ", pictureContentType='" + getPictureContentType() + "'" +
            "}";
    }
}
