package com.giocosmiano.dvdrental.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.giocosmiano.dvdrental.domain.FilmCategory} entity.
 */
public class FilmCategoryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Instant lastUpdate;

    private FilmDTO film;

    private CategoryDTO category;

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

    public FilmDTO getFilm() {
        return film;
    }

    public void setFilm(FilmDTO film) {
        this.film = film;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilmCategoryDTO)) {
            return false;
        }

        FilmCategoryDTO filmCategoryDTO = (FilmCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, filmCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmCategoryDTO{" +
            "id=" + getId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", film=" + getFilm() +
            ", category=" + getCategory() +
            "}";
    }
}
