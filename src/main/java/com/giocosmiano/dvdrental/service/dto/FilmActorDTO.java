package com.giocosmiano.dvdrental.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.giocosmiano.dvdrental.domain.FilmActor} entity.
 */
public class FilmActorDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant lastUpdate;

    private ActorDTO actor;

    private FilmDTO film;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ActorDTO getActor() {
        return actor;
    }

    public void setActor(ActorDTO actor) {
        this.actor = actor;
    }

    public FilmDTO getFilm() {
        return film;
    }

    public void setFilm(FilmDTO film) {
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilmActorDTO)) {
            return false;
        }

        FilmActorDTO filmActorDTO = (FilmActorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, filmActorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmActorDTO{" +
            "id=" + getId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", actor=" + getActor() +
            ", film=" + getFilm() +
            "}";
    }
}
