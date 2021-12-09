package com.giocosmiano.dvdrental.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Film.
 */
@Table("film")
public class Film implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("film_id")
    private Integer filmId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("title")
    private String title;

    @Size(max = 255)
    @Column("description")
    private String description;

    @Column("release_year")
    private Integer releaseYear;

    @NotNull(message = "must not be null")
    @Column("rental_duration")
    private Integer rentalDuration;

    @NotNull(message = "must not be null")
    @Column("rental_rate")
    private BigDecimal rentalRate;

    @Column("length")
    private Integer length;

    @NotNull(message = "must not be null")
    @Column("replacement_cost")
    private BigDecimal replacementCost;

    @Size(max = 255)
    @Column("rating")
    private String rating;

    @NotNull(message = "must not be null")
    @Column("last_update")
    private Instant lastUpdate;

    @Size(max = 255)
    @Column("special_features")
    private String specialFeatures;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("fulltext")
    private String fulltext;

    @Transient
    @JsonIgnoreProperties(value = { "films" }, allowSetters = true)
    private Language language;

    @Transient
    @JsonIgnoreProperties(value = { "actor", "film" }, allowSetters = true)
    private Set<FilmActor> filmActors = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "film", "category" }, allowSetters = true)
    private Set<FilmCategory> filmCategories = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "film", "rentals" }, allowSetters = true)
    private Set<Inventory> inventories = new HashSet<>();

    @Column("language_id")
    private Long languageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Film id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFilmId() {
        return this.filmId;
    }

    public Film filmId(Integer filmId) {
        this.setFilmId(filmId);
        return this;
    }

    public void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return this.title;
    }

    public Film title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Film description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return this.releaseYear;
    }

    public Film releaseYear(Integer releaseYear) {
        this.setReleaseYear(releaseYear);
        return this;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getRentalDuration() {
        return this.rentalDuration;
    }

    public Film rentalDuration(Integer rentalDuration) {
        this.setRentalDuration(rentalDuration);
        return this;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return this.rentalRate;
    }

    public Film rentalRate(BigDecimal rentalRate) {
        this.setRentalRate(rentalRate);
        return this;
    }

    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate != null ? rentalRate.stripTrailingZeros() : null;
    }

    public Integer getLength() {
        return this.length;
    }

    public Film length(Integer length) {
        this.setLength(length);
        return this;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public BigDecimal getReplacementCost() {
        return this.replacementCost;
    }

    public Film replacementCost(BigDecimal replacementCost) {
        this.setReplacementCost(replacementCost);
        return this;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost != null ? replacementCost.stripTrailingZeros() : null;
    }

    public String getRating() {
        return this.rating;
    }

    public Film rating(String rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public Film lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSpecialFeatures() {
        return this.specialFeatures;
    }

    public Film specialFeatures(String specialFeatures) {
        this.setSpecialFeatures(specialFeatures);
        return this;
    }

    public void setSpecialFeatures(String specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public String getFulltext() {
        return this.fulltext;
    }

    public Film fulltext(String fulltext) {
        this.setFulltext(fulltext);
        return this;
    }

    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language language) {
        this.language = language;
        this.languageId = language != null ? language.getId() : null;
    }

    public Film language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public Set<FilmActor> getFilmActors() {
        return this.filmActors;
    }

    public void setFilmActors(Set<FilmActor> filmActors) {
        if (this.filmActors != null) {
            this.filmActors.forEach(i -> i.setFilm(null));
        }
        if (filmActors != null) {
            filmActors.forEach(i -> i.setFilm(this));
        }
        this.filmActors = filmActors;
    }

    public Film filmActors(Set<FilmActor> filmActors) {
        this.setFilmActors(filmActors);
        return this;
    }

    public Film addFilmActor(FilmActor filmActor) {
        this.filmActors.add(filmActor);
        filmActor.setFilm(this);
        return this;
    }

    public Film removeFilmActor(FilmActor filmActor) {
        this.filmActors.remove(filmActor);
        filmActor.setFilm(null);
        return this;
    }

    public Set<FilmCategory> getFilmCategories() {
        return this.filmCategories;
    }

    public void setFilmCategories(Set<FilmCategory> filmCategories) {
        if (this.filmCategories != null) {
            this.filmCategories.forEach(i -> i.setFilm(null));
        }
        if (filmCategories != null) {
            filmCategories.forEach(i -> i.setFilm(this));
        }
        this.filmCategories = filmCategories;
    }

    public Film filmCategories(Set<FilmCategory> filmCategories) {
        this.setFilmCategories(filmCategories);
        return this;
    }

    public Film addFilmCategory(FilmCategory filmCategory) {
        this.filmCategories.add(filmCategory);
        filmCategory.setFilm(this);
        return this;
    }

    public Film removeFilmCategory(FilmCategory filmCategory) {
        this.filmCategories.remove(filmCategory);
        filmCategory.setFilm(null);
        return this;
    }

    public Set<Inventory> getInventories() {
        return this.inventories;
    }

    public void setInventories(Set<Inventory> inventories) {
        if (this.inventories != null) {
            this.inventories.forEach(i -> i.setFilm(null));
        }
        if (inventories != null) {
            inventories.forEach(i -> i.setFilm(this));
        }
        this.inventories = inventories;
    }

    public Film inventories(Set<Inventory> inventories) {
        this.setInventories(inventories);
        return this;
    }

    public Film addInventory(Inventory inventory) {
        this.inventories.add(inventory);
        inventory.setFilm(this);
        return this;
    }

    public Film removeInventory(Inventory inventory) {
        this.inventories.remove(inventory);
        inventory.setFilm(null);
        return this;
    }

    public Long getLanguageId() {
        return this.languageId;
    }

    public void setLanguageId(Long language) {
        this.languageId = language;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Film)) {
            return false;
        }
        return id != null && id.equals(((Film) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Film{" +
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
            "}";
    }
}
