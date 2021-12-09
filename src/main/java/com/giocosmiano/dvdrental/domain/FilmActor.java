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
 * A FilmActor.
 */
@Table("film_actor")
public class FilmActor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "filmActors" }, allowSetters = true)
    private Actor actor;

    @Transient
    @JsonIgnoreProperties(value = { "language", "filmActors", "filmCategories", "inventories" }, allowSetters = true)
    private Film film;

    @Column("actor_id")
    private Long actorId;

    @Column("film_id")
    private Long filmId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FilmActor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public FilmActor lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Actor getActor() {
        return this.actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        this.actorId = actor != null ? actor.getId() : null;
    }

    public FilmActor actor(Actor actor) {
        this.setActor(actor);
        return this;
    }

    public Film getFilm() {
        return this.film;
    }

    public void setFilm(Film film) {
        this.film = film;
        this.filmId = film != null ? film.getId() : null;
    }

    public FilmActor film(Film film) {
        this.setFilm(film);
        return this;
    }

    public Long getActorId() {
        return this.actorId;
    }

    public void setActorId(Long actor) {
        this.actorId = actor;
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
        if (!(o instanceof FilmActor)) {
            return false;
        }
        return id != null && id.equals(((FilmActor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmActor{" +
            "id=" + getId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
