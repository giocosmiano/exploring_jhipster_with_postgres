package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Category;
import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.domain.FilmCategory;
import com.giocosmiano.dvdrental.repository.FilmCategoryRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.FilmCategoryDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmCategoryMapper;
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
 * Integration tests for the {@link FilmCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class FilmCategoryResourceIT {

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/film-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FilmCategoryRepository filmCategoryRepository;

    @Autowired
    private FilmCategoryMapper filmCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FilmCategory filmCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FilmCategory createEntity(EntityManager em) {
        FilmCategory filmCategory = new FilmCategory().lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        Film film;
        film = em.insert(FilmResourceIT.createEntity(em)).block();
        filmCategory.setFilm(film);
        // Add required entity
        Category category;
        category = em.insert(CategoryResourceIT.createEntity(em)).block();
        filmCategory.setCategory(category);
        return filmCategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FilmCategory createUpdatedEntity(EntityManager em) {
        FilmCategory filmCategory = new FilmCategory().lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        Film film;
        film = em.insert(FilmResourceIT.createUpdatedEntity(em)).block();
        filmCategory.setFilm(film);
        // Add required entity
        Category category;
        category = em.insert(CategoryResourceIT.createUpdatedEntity(em)).block();
        filmCategory.setCategory(category);
        return filmCategory;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FilmCategory.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        FilmResourceIT.deleteEntities(em);
        CategoryResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        filmCategory = createEntity(em);
    }

    @Test
    void createFilmCategory() throws Exception {
        int databaseSizeBeforeCreate = filmCategoryRepository.findAll().collectList().block().size();
        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        FilmCategory testFilmCategory = filmCategoryList.get(filmCategoryList.size() - 1);
        assertThat(testFilmCategory.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createFilmCategoryWithExistingId() throws Exception {
        // Create the FilmCategory with an existing ID
        filmCategory.setId(1L);
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        int databaseSizeBeforeCreate = filmCategoryRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmCategoryRepository.findAll().collectList().block().size();
        // set the field null
        filmCategory.setLastUpdate(null);

        // Create the FilmCategory, which fails.
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFilmCategories() {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        // Get all the filmCategoryList
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
            .value(hasItem(filmCategory.getId().intValue()))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getFilmCategory() {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        // Get the filmCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, filmCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(filmCategory.getId().intValue()))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingFilmCategory() {
        // Get the filmCategory
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFilmCategory() throws Exception {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();

        // Update the filmCategory
        FilmCategory updatedFilmCategory = filmCategoryRepository.findById(filmCategory.getId()).block();
        updatedFilmCategory.lastUpdate(UPDATED_LAST_UPDATE);
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(updatedFilmCategory);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
        FilmCategory testFilmCategory = filmCategoryList.get(filmCategoryList.size() - 1);
        assertThat(testFilmCategory.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmCategoryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFilmCategoryWithPatch() throws Exception {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();

        // Update the filmCategory using partial update
        FilmCategory partialUpdatedFilmCategory = new FilmCategory();
        partialUpdatedFilmCategory.setId(filmCategory.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilmCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilmCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
        FilmCategory testFilmCategory = filmCategoryList.get(filmCategoryList.size() - 1);
        assertThat(testFilmCategory.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void fullUpdateFilmCategoryWithPatch() throws Exception {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();

        // Update the filmCategory using partial update
        FilmCategory partialUpdatedFilmCategory = new FilmCategory();
        partialUpdatedFilmCategory.setId(filmCategory.getId());

        partialUpdatedFilmCategory.lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilmCategory.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilmCategory))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
        FilmCategory testFilmCategory = filmCategoryList.get(filmCategoryList.size() - 1);
        assertThat(testFilmCategory.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, filmCategoryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFilmCategory() throws Exception {
        int databaseSizeBeforeUpdate = filmCategoryRepository.findAll().collectList().block().size();
        filmCategory.setId(count.incrementAndGet());

        // Create the FilmCategory
        FilmCategoryDTO filmCategoryDTO = filmCategoryMapper.toDto(filmCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmCategoryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FilmCategory in the database
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFilmCategory() {
        // Initialize the database
        filmCategoryRepository.save(filmCategory).block();

        int databaseSizeBeforeDelete = filmCategoryRepository.findAll().collectList().block().size();

        // Delete the filmCategory
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, filmCategory.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<FilmCategory> filmCategoryList = filmCategoryRepository.findAll().collectList().block();
        assertThat(filmCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
