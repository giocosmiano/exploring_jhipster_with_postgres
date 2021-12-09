package com.giocosmiano.dvdrental.web.rest;

import static com.giocosmiano.dvdrental.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.domain.Language;
import com.giocosmiano.dvdrental.repository.FilmRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.FilmDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmMapper;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link FilmResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class FilmResourceIT {

    private static final Integer DEFAULT_FILM_ID = 1;
    private static final Integer UPDATED_FILM_ID = 2;

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_RELEASE_YEAR = 1;
    private static final Integer UPDATED_RELEASE_YEAR = 2;

    private static final Integer DEFAULT_RENTAL_DURATION = 1;
    private static final Integer UPDATED_RENTAL_DURATION = 2;

    private static final BigDecimal DEFAULT_RENTAL_RATE = new BigDecimal(1);
    private static final BigDecimal UPDATED_RENTAL_RATE = new BigDecimal(2);

    private static final Integer DEFAULT_LENGTH = 1;
    private static final Integer UPDATED_LENGTH = 2;

    private static final BigDecimal DEFAULT_REPLACEMENT_COST = new BigDecimal(1);
    private static final BigDecimal UPDATED_REPLACEMENT_COST = new BigDecimal(2);

    private static final String DEFAULT_RATING = "AAAAAAAAAA";
    private static final String UPDATED_RATING = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SPECIAL_FEATURES = "AAAAAAAAAA";
    private static final String UPDATED_SPECIAL_FEATURES = "BBBBBBBBBB";

    private static final String DEFAULT_FULLTEXT = "AAAAAAAAAA";
    private static final String UPDATED_FULLTEXT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/films";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmMapper filmMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Film film;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createEntity(EntityManager em) {
        Film film = new Film()
            .filmId(DEFAULT_FILM_ID)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .releaseYear(DEFAULT_RELEASE_YEAR)
            .rentalDuration(DEFAULT_RENTAL_DURATION)
            .rentalRate(DEFAULT_RENTAL_RATE)
            .length(DEFAULT_LENGTH)
            .replacementCost(DEFAULT_REPLACEMENT_COST)
            .rating(DEFAULT_RATING)
            .lastUpdate(DEFAULT_LAST_UPDATE)
            .specialFeatures(DEFAULT_SPECIAL_FEATURES)
            .fulltext(DEFAULT_FULLTEXT);
        // Add required entity
        Language language;
        language = em.insert(LanguageResourceIT.createEntity(em)).block();
        film.setLanguage(language);
        return film;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Film createUpdatedEntity(EntityManager em) {
        Film film = new Film()
            .filmId(UPDATED_FILM_ID)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .releaseYear(UPDATED_RELEASE_YEAR)
            .rentalDuration(UPDATED_RENTAL_DURATION)
            .rentalRate(UPDATED_RENTAL_RATE)
            .length(UPDATED_LENGTH)
            .replacementCost(UPDATED_REPLACEMENT_COST)
            .rating(UPDATED_RATING)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .specialFeatures(UPDATED_SPECIAL_FEATURES)
            .fulltext(UPDATED_FULLTEXT);
        // Add required entity
        Language language;
        language = em.insert(LanguageResourceIT.createUpdatedEntity(em)).block();
        film.setLanguage(language);
        return film;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Film.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        LanguageResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        film = createEntity(em);
    }

    @Test
    void createFilm() throws Exception {
        int databaseSizeBeforeCreate = filmRepository.findAll().collectList().block().size();
        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate + 1);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getFilmId()).isEqualTo(DEFAULT_FILM_ID);
        assertThat(testFilm.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFilm.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFilm.getReleaseYear()).isEqualTo(DEFAULT_RELEASE_YEAR);
        assertThat(testFilm.getRentalDuration()).isEqualTo(DEFAULT_RENTAL_DURATION);
        assertThat(testFilm.getRentalRate()).isEqualByComparingTo(DEFAULT_RENTAL_RATE);
        assertThat(testFilm.getLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testFilm.getReplacementCost()).isEqualByComparingTo(DEFAULT_REPLACEMENT_COST);
        assertThat(testFilm.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testFilm.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
        assertThat(testFilm.getSpecialFeatures()).isEqualTo(DEFAULT_SPECIAL_FEATURES);
        assertThat(testFilm.getFulltext()).isEqualTo(DEFAULT_FULLTEXT);
    }

    @Test
    void createFilmWithExistingId() throws Exception {
        // Create the Film with an existing ID
        film.setId(1L);
        FilmDTO filmDTO = filmMapper.toDto(film);

        int databaseSizeBeforeCreate = filmRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFilmIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setFilmId(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setTitle(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRentalDurationIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setRentalDuration(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRentalRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setRentalRate(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkReplacementCostIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setReplacementCost(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setLastUpdate(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFulltextIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmRepository.findAll().collectList().block().size();
        // set the field null
        film.setFulltext(null);

        // Create the Film, which fails.
        FilmDTO filmDTO = filmMapper.toDto(film);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFilms() {
        // Initialize the database
        filmRepository.save(film).block();

        // Get all the filmList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(film.getId().intValue()))
            .jsonPath("$.[*].filmId")
            .value(hasItem(DEFAULT_FILM_ID))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].releaseYear")
            .value(hasItem(DEFAULT_RELEASE_YEAR))
            .jsonPath("$.[*].rentalDuration")
            .value(hasItem(DEFAULT_RENTAL_DURATION))
            .jsonPath("$.[*].rentalRate")
            .value(hasItem(sameNumber(DEFAULT_RENTAL_RATE)))
            .jsonPath("$.[*].length")
            .value(hasItem(DEFAULT_LENGTH))
            .jsonPath("$.[*].replacementCost")
            .value(hasItem(sameNumber(DEFAULT_REPLACEMENT_COST)))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()))
            .jsonPath("$.[*].specialFeatures")
            .value(hasItem(DEFAULT_SPECIAL_FEATURES))
            .jsonPath("$.[*].fulltext")
            .value(hasItem(DEFAULT_FULLTEXT));
    }

    @Test
    void getFilm() {
        // Initialize the database
        filmRepository.save(film).block();

        // Get the film
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, film.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(film.getId().intValue()))
            .jsonPath("$.filmId")
            .value(is(DEFAULT_FILM_ID))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.releaseYear")
            .value(is(DEFAULT_RELEASE_YEAR))
            .jsonPath("$.rentalDuration")
            .value(is(DEFAULT_RENTAL_DURATION))
            .jsonPath("$.rentalRate")
            .value(is(sameNumber(DEFAULT_RENTAL_RATE)))
            .jsonPath("$.length")
            .value(is(DEFAULT_LENGTH))
            .jsonPath("$.replacementCost")
            .value(is(sameNumber(DEFAULT_REPLACEMENT_COST)))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()))
            .jsonPath("$.specialFeatures")
            .value(is(DEFAULT_SPECIAL_FEATURES))
            .jsonPath("$.fulltext")
            .value(is(DEFAULT_FULLTEXT));
    }

    @Test
    void getNonExistingFilm() {
        // Get the film
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFilm() throws Exception {
        // Initialize the database
        filmRepository.save(film).block();

        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();

        // Update the film
        Film updatedFilm = filmRepository.findById(film.getId()).block();
        updatedFilm
            .filmId(UPDATED_FILM_ID)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .releaseYear(UPDATED_RELEASE_YEAR)
            .rentalDuration(UPDATED_RENTAL_DURATION)
            .rentalRate(UPDATED_RENTAL_RATE)
            .length(UPDATED_LENGTH)
            .replacementCost(UPDATED_REPLACEMENT_COST)
            .rating(UPDATED_RATING)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .specialFeatures(UPDATED_SPECIAL_FEATURES)
            .fulltext(UPDATED_FULLTEXT);
        FilmDTO filmDTO = filmMapper.toDto(updatedFilm);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getFilmId()).isEqualTo(UPDATED_FILM_ID);
        assertThat(testFilm.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFilm.getReleaseYear()).isEqualTo(UPDATED_RELEASE_YEAR);
        assertThat(testFilm.getRentalDuration()).isEqualTo(UPDATED_RENTAL_DURATION);
        assertThat(testFilm.getRentalRate()).isEqualTo(UPDATED_RENTAL_RATE);
        assertThat(testFilm.getLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testFilm.getReplacementCost()).isEqualTo(UPDATED_REPLACEMENT_COST);
        assertThat(testFilm.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testFilm.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testFilm.getSpecialFeatures()).isEqualTo(UPDATED_SPECIAL_FEATURES);
        assertThat(testFilm.getFulltext()).isEqualTo(UPDATED_FULLTEXT);
    }

    @Test
    void putNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.save(film).block();

        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm
            .description(UPDATED_DESCRIPTION)
            .rentalDuration(UPDATED_RENTAL_DURATION)
            .rentalRate(UPDATED_RENTAL_RATE)
            .replacementCost(UPDATED_REPLACEMENT_COST)
            .rating(UPDATED_RATING)
            .lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getFilmId()).isEqualTo(DEFAULT_FILM_ID);
        assertThat(testFilm.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFilm.getReleaseYear()).isEqualTo(DEFAULT_RELEASE_YEAR);
        assertThat(testFilm.getRentalDuration()).isEqualTo(UPDATED_RENTAL_DURATION);
        assertThat(testFilm.getRentalRate()).isEqualByComparingTo(UPDATED_RENTAL_RATE);
        assertThat(testFilm.getLength()).isEqualTo(DEFAULT_LENGTH);
        assertThat(testFilm.getReplacementCost()).isEqualByComparingTo(UPDATED_REPLACEMENT_COST);
        assertThat(testFilm.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testFilm.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testFilm.getSpecialFeatures()).isEqualTo(DEFAULT_SPECIAL_FEATURES);
        assertThat(testFilm.getFulltext()).isEqualTo(DEFAULT_FULLTEXT);
    }

    @Test
    void fullUpdateFilmWithPatch() throws Exception {
        // Initialize the database
        filmRepository.save(film).block();

        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();

        // Update the film using partial update
        Film partialUpdatedFilm = new Film();
        partialUpdatedFilm.setId(film.getId());

        partialUpdatedFilm
            .filmId(UPDATED_FILM_ID)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .releaseYear(UPDATED_RELEASE_YEAR)
            .rentalDuration(UPDATED_RENTAL_DURATION)
            .rentalRate(UPDATED_RENTAL_RATE)
            .length(UPDATED_LENGTH)
            .replacementCost(UPDATED_REPLACEMENT_COST)
            .rating(UPDATED_RATING)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .specialFeatures(UPDATED_SPECIAL_FEATURES)
            .fulltext(UPDATED_FULLTEXT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilm.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilm))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
        Film testFilm = filmList.get(filmList.size() - 1);
        assertThat(testFilm.getFilmId()).isEqualTo(UPDATED_FILM_ID);
        assertThat(testFilm.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFilm.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFilm.getReleaseYear()).isEqualTo(UPDATED_RELEASE_YEAR);
        assertThat(testFilm.getRentalDuration()).isEqualTo(UPDATED_RENTAL_DURATION);
        assertThat(testFilm.getRentalRate()).isEqualByComparingTo(UPDATED_RENTAL_RATE);
        assertThat(testFilm.getLength()).isEqualTo(UPDATED_LENGTH);
        assertThat(testFilm.getReplacementCost()).isEqualByComparingTo(UPDATED_REPLACEMENT_COST);
        assertThat(testFilm.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testFilm.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testFilm.getSpecialFeatures()).isEqualTo(UPDATED_SPECIAL_FEATURES);
        assertThat(testFilm.getFulltext()).isEqualTo(UPDATED_FULLTEXT);
    }

    @Test
    void patchNonExistingFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, filmDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFilm() throws Exception {
        int databaseSizeBeforeUpdate = filmRepository.findAll().collectList().block().size();
        film.setId(count.incrementAndGet());

        // Create the Film
        FilmDTO filmDTO = filmMapper.toDto(film);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Film in the database
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFilm() {
        // Initialize the database
        filmRepository.save(film).block();

        int databaseSizeBeforeDelete = filmRepository.findAll().collectList().block().size();

        // Delete the film
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, film.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Film> filmList = filmRepository.findAll().collectList().block();
        assertThat(filmList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
