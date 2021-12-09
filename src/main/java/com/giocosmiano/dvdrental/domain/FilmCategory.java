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
 * A FilmCategory.
 */
@Table("film_category")
public class FilmCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "language", "filmActors", "filmCategories", "inventories" }, allowSetters = true)
    private Film film;

    @Transient
    @JsonIgnoreProperties(value = { "filmCategories" }, allowSetters = true)
    private Category category;

    @Column("film_id")
    private Long filmId;

    @Column("category_id")
    private Long categoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FilmCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public FilmCategory lastUpdate(Instant lastUpdate) {
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

    public FilmCategory film(Film film) {
        this.setFilm(film);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.categoryId = category != null ? category.getId() : null;
    }

    public FilmCategory category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Long getFilmId() {
        return this.filmId;
    }

    public void setFilmId(Long film) {
        this.filmId = film;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long category) {
        this.categoryId = category;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilmCategory)) {
            return false;
        }
        return id != null && id.equals(((FilmCategory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmCategory{" +
            "id=" + getId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
