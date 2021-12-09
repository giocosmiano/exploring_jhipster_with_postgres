package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Actor;
import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.domain.FilmActor;
import com.giocosmiano.dvdrental.repository.FilmActorRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.FilmActorDTO;
import com.giocosmiano.dvdrental.service.mapper.FilmActorMapper;
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
 * Integration tests for the {@link FilmActorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class FilmActorResourceIT {

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/film-actors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FilmActorRepository filmActorRepository;

    @Autowired
    private FilmActorMapper filmActorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FilmActor filmActor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FilmActor createEntity(EntityManager em) {
        FilmActor filmActor = new FilmActor().lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        Actor actor;
        actor = em.insert(ActorResourceIT.createEntity(em)).block();
        filmActor.setActor(actor);
        // Add required entity
        Film film;
        film = em.insert(FilmResourceIT.createEntity(em)).block();
        filmActor.setFilm(film);
        return filmActor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FilmActor createUpdatedEntity(EntityManager em) {
        FilmActor filmActor = new FilmActor().lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        Actor actor;
        actor = em.insert(ActorResourceIT.createUpdatedEntity(em)).block();
        filmActor.setActor(actor);
        // Add required entity
        Film film;
        film = em.insert(FilmResourceIT.createUpdatedEntity(em)).block();
        filmActor.setFilm(film);
        return filmActor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FilmActor.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ActorResourceIT.deleteEntities(em);
        FilmResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        filmActor = createEntity(em);
    }

    @Test
    void createFilmActor() throws Exception {
        int databaseSizeBeforeCreate = filmActorRepository.findAll().collectList().block().size();
        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeCreate + 1);
        FilmActor testFilmActor = filmActorList.get(filmActorList.size() - 1);
        assertThat(testFilmActor.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createFilmActorWithExistingId() throws Exception {
        // Create the FilmActor with an existing ID
        filmActor.setId(1L);
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        int databaseSizeBeforeCreate = filmActorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = filmActorRepository.findAll().collectList().block().size();
        // set the field null
        filmActor.setLastUpdate(null);

        // Create the FilmActor, which fails.
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllFilmActors() {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        // Get all the filmActorList
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
            .value(hasItem(filmActor.getId().intValue()))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getFilmActor() {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        // Get the filmActor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, filmActor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(filmActor.getId().intValue()))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingFilmActor() {
        // Get the filmActor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFilmActor() throws Exception {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();

        // Update the filmActor
        FilmActor updatedFilmActor = filmActorRepository.findById(filmActor.getId()).block();
        updatedFilmActor.lastUpdate(UPDATED_LAST_UPDATE);
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(updatedFilmActor);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmActorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
        FilmActor testFilmActor = filmActorList.get(filmActorList.size() - 1);
        assertThat(testFilmActor.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filmActorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFilmActorWithPatch() throws Exception {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();

        // Update the filmActor using partial update
        FilmActor partialUpdatedFilmActor = new FilmActor();
        partialUpdatedFilmActor.setId(filmActor.getId());

        partialUpdatedFilmActor.lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilmActor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilmActor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
        FilmActor testFilmActor = filmActorList.get(filmActorList.size() - 1);
        assertThat(testFilmActor.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void fullUpdateFilmActorWithPatch() throws Exception {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();

        // Update the filmActor using partial update
        FilmActor partialUpdatedFilmActor = new FilmActor();
        partialUpdatedFilmActor.setId(filmActor.getId());

        partialUpdatedFilmActor.lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFilmActor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFilmActor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
        FilmActor testFilmActor = filmActorList.get(filmActorList.size() - 1);
        assertThat(testFilmActor.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, filmActorDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFilmActor() throws Exception {
        int databaseSizeBeforeUpdate = filmActorRepository.findAll().collectList().block().size();
        filmActor.setId(count.incrementAndGet());

        // Create the FilmActor
        FilmActorDTO filmActorDTO = filmActorMapper.toDto(filmActor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filmActorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FilmActor in the database
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFilmActor() {
        // Initialize the database
        filmActorRepository.save(filmActor).block();

        int databaseSizeBeforeDelete = filmActorRepository.findAll().collectList().block().size();

        // Delete the filmActor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, filmActor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<FilmActor> filmActorList = filmActorRepository.findAll().collectList().block();
        assertThat(filmActorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
