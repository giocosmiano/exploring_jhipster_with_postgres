package com.giocosmiano.dvdrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Store.
 */
@Table("store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("store_id")
    private Integer storeId;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals", "stores" }, allowSetters = true)
    private Staff managerStaff;

    @Transient
    @JsonIgnoreProperties(value = { "city", "customers", "staff", "stores" }, allowSetters = true)
    private Address address;

    @Column("manager_staff_id")
    private Long managerStaffId;

    @Column("address_id")
    private Long addressId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Store id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return this.storeId;
    }

    public Store storeId(Integer storeId) {
        this.setStoreId(storeId);
        return this;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Store lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Staff getManagerStaff() {
        return this.managerStaff;
    }

    public void setManagerStaff(Staff staff) {
        this.managerStaff = staff;
        this.managerStaffId = staff != null ? staff.getId() : null;
    }

    public Store managerStaff(Staff staff) {
        this.setManagerStaff(staff);
        return this;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
        this.addressId = address != null ? address.getId() : null;
    }

    public Store address(Address address) {
        this.setAddress(address);
        return this;
    }

    public Long getManagerStaffId() {
        return this.managerStaffId;
    }

    public void setManagerStaffId(Long staff) {
        this.managerStaffId = staff;
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
        if (!(o instanceof Store)) {
            return false;
        }
        return id != null && id.equals(((Store) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Store{" +
            "id=" + getId() +
            ", storeId=" + getStoreId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
