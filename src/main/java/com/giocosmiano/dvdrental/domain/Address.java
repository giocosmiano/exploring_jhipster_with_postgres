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
 * A Address.
 */
@Table("address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("address_id")
    private Integer addressId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("address")
    private String address;

    @Size(max = 255)
    @Column("address_2")
    private String address2;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("district")
    private String district;

    @Size(max = 255)
    @Column("postal_code")
    private String postalCode;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("phone")
    private String phone;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "country", "addresses" }, allowSetters = true)
    private City city;

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals" }, allowSetters = true)
    private Set<Customer> customers = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "address", "payments", "rentals", "stores" }, allowSetters = true)
    private Set<Staff> staff = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "managerStaff", "address" }, allowSetters = true)
    private Set<Store> stores = new HashSet<>();

    @Column("city_id")
    private Long cityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Address id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAddressId() {
        return this.addressId;
    }

    public Address addressId(Integer addressId) {
        this.setAddressId(addressId);
        return this;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return this.address;
    }

    public Address address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return this.address2;
    }

    public Address address2(String address2) {
        this.setAddress2(address2);
        return this;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDistrict() {
        return this.district;
    }

    public Address district(String district) {
        this.setDistrict(district);
        return this;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public Address postalCode(String postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return this.phone;
    }

    public Address phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Address lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city != null ? city.getId() : null;
    }

    public Address city(City city) {
        this.setCity(city);
        return this;
    }

    public Set<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        if (this.customers != null) {
            this.customers.forEach(i -> i.setAddress(null));
        }
        if (customers != null) {
            customers.forEach(i -> i.setAddress(this));
        }
        this.customers = customers;
    }

    public Address customers(Set<Customer> customers) {
        this.setCustomers(customers);
        return this;
    }

    public Address addCustomer(Customer customer) {
        this.customers.add(customer);
        customer.setAddress(this);
        return this;
    }

    public Address removeCustomer(Customer customer) {
        this.customers.remove(customer);
        customer.setAddress(null);
        return this;
    }

    public Set<Staff> getStaff() {
        return this.staff;
    }

    public void setStaff(Set<Staff> staff) {
        if (this.staff != null) {
            this.staff.forEach(i -> i.setAddress(null));
        }
        if (staff != null) {
            staff.forEach(i -> i.setAddress(this));
        }
        this.staff = staff;
    }

    public Address staff(Set<Staff> staff) {
        this.setStaff(staff);
        return this;
    }

    public Address addStaff(Staff staff) {
        this.staff.add(staff);
        staff.setAddress(this);
        return this;
    }

    public Address removeStaff(Staff staff) {
        this.staff.remove(staff);
        staff.setAddress(null);
        return this;
    }

    public Set<Store> getStores() {
        return this.stores;
    }

    public void setStores(Set<Store> stores) {
        if (this.stores != null) {
            this.stores.forEach(i -> i.setAddress(null));
        }
        if (stores != null) {
            stores.forEach(i -> i.setAddress(this));
        }
        this.stores = stores;
    }

    public Address stores(Set<Store> stores) {
        this.setStores(stores);
        return this;
    }

    public Address addStore(Store store) {
        this.stores.add(store);
        store.setAddress(this);
        return this;
    }

    public Address removeStore(Store store) {
        this.stores.remove(store);
        store.setAddress(null);
        return this;
    }

    public Long getCityId() {
        return this.cityId;
    }

    public void setCityId(Long city) {
        this.cityId = city;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        return id != null && id.equals(((Address) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Address{" +
            "id=" + getId() +
            ", addressId=" + getAddressId() +
            ", address='" + getAddress() + "'" +
            ", address2='" + getAddress2() + "'" +
            ", district='" + getDistrict() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", phone='" + getPhone() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
