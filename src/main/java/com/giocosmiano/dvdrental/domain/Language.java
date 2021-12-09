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
 * A Language.
 */
@Table("language")
public class Language implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("language_id")
    private Integer languageId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "language", "filmActors", "filmCategories", "inventories" }, allowSetters = true)
    private Set<Film> films = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Language id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLanguageId() {
        return this.languageId;
    }

    public Language languageId(Integer languageId) {
        this.setLanguageId(languageId);
        return this;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return this.name;
    }

    public Language name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Language lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Film> getFilms() {
        return this.films;
    }

    public void setFilms(Set<Film> films) {
        if (this.films != null) {
            this.films.forEach(i -> i.setLanguage(null));
        }
        if (films != null) {
            films.forEach(i -> i.setLanguage(this));
        }
        this.films = films;
    }

    public Language films(Set<Film> films) {
        this.setFilms(films);
        return this;
    }

    public Language addFilm(Film film) {
        this.films.add(film);
        film.setLanguage(this);
        return this;
    }

    public Language removeFilm(Film film) {
        this.films.remove(film);
        film.setLanguage(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Language)) {
            return false;
        }
        return id != null && id.equals(((Language) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Language{" +
            "id=" + getId() +
            ", languageId=" + getLanguageId() +
            ", name='" + getName() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
