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
 * A City.
 */
@Table("city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("city_id")
    private Integer cityId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("city")
    private String city;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "cities" }, allowSetters = true)
    private Country country;

    @Transient
    @JsonIgnoreProperties(value = { "city", "customers", "staff", "stores" }, allowSetters = true)
    private Set<Address> addresses = new HashSet<>();

    @Column("country_id")
    private Long countryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public City id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCityId() {
        return this.cityId;
    }

    public City cityId(Integer cityId) {
        this.setCityId(cityId);
        return this;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return this.city;
    }

    public City city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public City lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.countryId = country != null ? country.getId() : null;
    }

    public City country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        if (this.addresses != null) {
            this.addresses.forEach(i -> i.setCity(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setCity(this));
        }
        this.addresses = addresses;
    }

    public City addresses(Set<Address> addresses) {
        this.setAddresses(addresses);
        return this;
    }

    public City addAddress(Address address) {
        this.addresses.add(address);
        address.setCity(this);
        return this;
    }

    public City removeAddress(Address address) {
        this.addresses.remove(address);
        address.setCity(null);
        return this;
    }

    public Long getCountryId() {
        return this.countryId;
    }

    public void setCountryId(Long country) {
        this.countryId = country;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof City)) {
            return false;
        }
        return id != null && id.equals(((City) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "City{" +
            "id=" + getId() +
            ", cityId=" + getCityId() +
            ", city='" + getCity() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
