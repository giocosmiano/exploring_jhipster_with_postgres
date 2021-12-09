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
 * A Inventory.
 */
@Table("inventory")
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("inventory_id")
    private Integer inventoryId;

    @NotNull(message = "must not be null")
    @Column("store_id")
    private Integer storeId;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "language", "filmActors", "filmCategories", "inventories" }, allowSetters = true)
    private Film film;

    @Transient
    @JsonIgnoreProperties(value = { "inventory", "customer", "staff", "payments" }, allowSetters = true)
    private Set<Rental> rentals = new HashSet<>();

    @Column("film_id")
    private Long filmId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Inventory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInventoryId() {
        return this.inventoryId;
    }

    public Inventory inventoryId(Integer inventoryId) {
        this.setInventoryId(inventoryId);
        return this;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getStoreId() {
        return this.storeId;
    }

    public Inventory storeId(Integer storeId) {
        this.setStoreId(storeId);
        return this;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Inventory lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Film getFilm() {
        return this.film;
    }

    public void setFilm(Film film) {
        this.film = film;
        this.filmId = film != null ? film.getId() : null;
    }

    public Inventory film(Film film) {
        this.setFilm(film);
        return this;
    }

    public Set<Rental> getRentals() {
        return this.rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        if (this.rentals != null) {
            this.rentals.forEach(i -> i.setInventory(null));
        }
        if (rentals != null) {
            rentals.forEach(i -> i.setInventory(this));
        }
        this.rentals = rentals;
    }

    public Inventory rentals(Set<Rental> rentals) {
        this.setRentals(rentals);
        return this;
    }

    public Inventory addRental(Rental rental) {
        this.rentals.add(rental);
        rental.setInventory(this);
        return this;
    }

    public Inventory removeRental(Rental rental) {
        this.rentals.remove(rental);
        rental.setInventory(null);
        return this;
    }

    public Long getFilmId() {
        return this.filmId;
    }

    public void setFilmId(Long film) {
        this.filmId = film;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inventory)) {
            return false;
        }
        return id != null && id.equals(((Inventory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inventory{" +
            "id=" + getId() +
            ", inventoryId=" + getInventoryId() +
            ", storeId=" + getStoreId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
