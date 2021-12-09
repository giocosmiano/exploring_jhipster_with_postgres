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
 * A Actor.
 */
@Table("actor")
public class Actor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("actor_id")
    private Integer actorId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("first_name")
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Transient
    @JsonIgnoreProperties(value = { "actor", "film" }, allowSetters = true)
    private Set<FilmActor> filmActors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Actor id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getActorId() {
        return this.actorId;
    }

    public Actor actorId(Integer actorId) {
        this.setActorId(actorId);
        return this;
    }

    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Actor firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Actor lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Actor lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<FilmActor> getFilmActors() {
        return this.filmActors;
    }

    public void setFilmActors(Set<FilmActor> filmActors) {
        if (this.filmActors != null) {
            this.filmActors.forEach(i -> i.setActor(null));
        }
        if (filmActors != null) {
            filmActors.forEach(i -> i.setActor(this));
        }
        this.filmActors = filmActors;
    }

    public Actor filmActors(Set<FilmActor> filmActors) {
        this.setFilmActors(filmActors);
        return this;
    }

    public Actor addFilmActor(FilmActor filmActor) {
        this.filmActors.add(filmActor);
        filmActor.setActor(this);
        return this;
    }

    public Actor removeFilmActor(FilmActor filmActor) {
        this.filmActors.remove(filmActor);
        filmActor.setActor(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Actor)) {
            return false;
        }
        return id != null && id.equals(((Actor) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Actor{" +
            "id=" + getId() +
            ", actorId=" + getActorId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
