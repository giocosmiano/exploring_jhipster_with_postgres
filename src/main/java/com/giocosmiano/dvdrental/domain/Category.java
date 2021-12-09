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
 * A Category.
 */
@Table("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("category_id")
    private Integer categoryId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "film", "category" }, allowSetters = true)
    private Set<FilmCategory> filmCategories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public Category categoryId(Integer categoryId) {
        this.setCategoryId(categoryId);
        return this;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return this.name;
    }

    public Category name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Category lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<FilmCategory> getFilmCategories() {
        return this.filmCategories;
    }

    public void setFilmCategories(Set<FilmCategory> filmCategories) {
        if (this.filmCategories != null) {
            this.filmCategories.forEach(i -> i.setCategory(null));
        }
        if (filmCategories != null) {
            filmCategories.forEach(i -> i.setCategory(this));
        }
        this.filmCategories = filmCategories;
    }

    public Category filmCategories(Set<FilmCategory> filmCategories) {
        this.setFilmCategories(filmCategories);
        return this;
    }

    public Category addFilmCategory(FilmCategory filmCategory) {
        this.filmCategories.add(filmCategory);
        filmCategory.setCategory(this);
        return this;
    }

    public Category removeFilmCategory(FilmCategory filmCategory) {
        this.filmCategories.remove(filmCategory);
        filmCategory.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", categoryId=" + getCategoryId() +
            ", name='" + getName() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
