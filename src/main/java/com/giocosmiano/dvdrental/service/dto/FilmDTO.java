package com.giocosmiano.dvdrental.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.giocosmiano.dvdrental.domain.Film} entity.
 */
public class FilmDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer filmId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String description;

    private Integer releaseYear;

    @NotNull(message = "must not be null")
    private Integer rentalDuration;

    @NotNull(message = "must not be null")
    private BigDecimal rentalRate;

    private Integer length;

    @NotNull(message = "must not be null")
    private BigDecimal replacementCost;

    @Size(max = 255)
    private String rating;

    @NotNull(message = "must not be null")
    private Instant lastUpdate;

    @Size(max = 255)
    private String specialFeatures;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String fulltext;

    private LanguageDTO language;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFilmId() {
        return filmId;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(String specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public String getFulltext() {
        return fulltext;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public LanguageDTO getLanguage() {
        return language;
    }

    public void setLanguage(LanguageDTO language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilmDTO)) {
            return false;
        }

        FilmDTO filmDTO = (FilmDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, filmDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FilmDTO{" +
            "id=" + getId() +
            ", filmId=" + getFilmId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", releaseYear=" + getReleaseYear() +
            ", rentalDuration=" + getRentalDuration() +
            ", rentalRate=" + getRentalRate() +
            ", length=" + getLength() +
            ", replacementCost=" + getReplacementCost() +
            ", rating='" + getRating() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", specialFeatures='" + getSpecialFeatures() + "'" +
            ", fulltext='" + getFulltext() + "'" +
            ", language=" + getLanguage() +
            "}";
    }
}
