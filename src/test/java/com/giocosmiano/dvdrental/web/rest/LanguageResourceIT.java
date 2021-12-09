package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Language;
import com.giocosmiano.dvdrental.repository.LanguageRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.LanguageDTO;
import com.giocosmiano.dvdrental.service.mapper.LanguageMapper;
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
 * Integration tests for the {@link LanguageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class LanguageResourceIT {

    private static final Integer DEFAULT_LANGUAGE_ID = 1;
    private static final Integer UPDATED_LANGUAGE_ID = 2;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/languages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private LanguageMapper languageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Language language;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Language createEntity(EntityManager em) {
        Language language = new Language().languageId(DEFAULT_LANGUAGE_ID).name(DEFAULT_NAME).lastUpdate(DEFAULT_LAST_UPDATE);
        return language;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Language createUpdatedEntity(EntityManager em) {
        Language language = new Language().languageId(UPDATED_LANGUAGE_ID).name(UPDATED_NAME).lastUpdate(UPDATED_LAST_UPDATE);
        return language;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Language.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        language = createEntity(em);
    }

    @Test
    void createLanguage() throws Exception {
        int databaseSizeBeforeCreate = languageRepository.findAll().collectList().block().size();
        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeCreate + 1);
        Language testLanguage = languageList.get(languageList.size() - 1);
        assertThat(testLanguage.getLanguageId()).isEqualTo(DEFAULT_LANGUAGE_ID);
        assertThat(testLanguage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLanguage.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createLanguageWithExistingId() throws Exception {
        // Create the Language with an existing ID
        language.setId(1L);
        LanguageDTO languageDTO = languageMapper.toDto(language);

        int databaseSizeBeforeCreate = languageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkLanguageIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = languageRepository.findAll().collectList().block().size();
        // set the field null
        language.setLanguageId(null);

        // Create the Language, which fails.
        LanguageDTO languageDTO = languageMapper.toDto(language);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = languageRepository.findAll().collectList().block().size();
        // set the field null
        language.setName(null);

        // Create the Language, which fails.
        LanguageDTO languageDTO = languageMapper.toDto(language);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = languageRepository.findAll().collectList().block().size();
        // set the field null
        language.setLastUpdate(null);

        // Create the Language, which fails.
        LanguageDTO languageDTO = languageMapper.toDto(language);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllLanguages() {
        // Initialize the database
        languageRepository.save(language).block();

        // Get all the languageList
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
            .value(hasItem(language.getId().intValue()))
            .jsonPath("$.[*].languageId")
            .value(hasItem(DEFAULT_LANGUAGE_ID))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getLanguage() {
        // Initialize the database
        languageRepository.save(language).block();

        // Get the language
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, language.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(language.getId().intValue()))
            .jsonPath("$.languageId")
            .value(is(DEFAULT_LANGUAGE_ID))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingLanguage() {
        // Get the language
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewLanguage() throws Exception {
        // Initialize the database
        languageRepository.save(language).block();

        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();

        // Update the language
        Language updatedLanguage = languageRepository.findById(language.getId()).block();
        updatedLanguage.languageId(UPDATED_LANGUAGE_ID).name(UPDATED_NAME).lastUpdate(UPDATED_LAST_UPDATE);
        LanguageDTO languageDTO = languageMapper.toDto(updatedLanguage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, languageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
        Language testLanguage = languageList.get(languageList.size() - 1);
        assertThat(testLanguage.getLanguageId()).isEqualTo(UPDATED_LANGUAGE_ID);
        assertThat(testLanguage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLanguage.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, languageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLanguageWithPatch() throws Exception {
        // Initialize the database
        languageRepository.save(language).block();

        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();

        // Update the language using partial update
        Language partialUpdatedLanguage = new Language();
        partialUpdatedLanguage.setId(language.getId());

        partialUpdatedLanguage.languageId(UPDATED_LANGUAGE_ID).lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLanguage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLanguage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
        Language testLanguage = languageList.get(languageList.size() - 1);
        assertThat(testLanguage.getLanguageId()).isEqualTo(UPDATED_LANGUAGE_ID);
        assertThat(testLanguage.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLanguage.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void fullUpdateLanguageWithPatch() throws Exception {
        // Initialize the database
        languageRepository.save(language).block();

        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();

        // Update the language using partial update
        Language partialUpdatedLanguage = new Language();
        partialUpdatedLanguage.setId(language.getId());

        partialUpdatedLanguage.languageId(UPDATED_LANGUAGE_ID).name(UPDATED_NAME).lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLanguage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLanguage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
        Language testLanguage = languageList.get(languageList.size() - 1);
        assertThat(testLanguage.getLanguageId()).isEqualTo(UPDATED_LANGUAGE_ID);
        assertThat(testLanguage.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLanguage.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, languageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLanguage() throws Exception {
        int databaseSizeBeforeUpdate = languageRepository.findAll().collectList().block().size();
        language.setId(count.incrementAndGet());

        // Create the Language
        LanguageDTO languageDTO = languageMapper.toDto(language);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(languageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Language in the database
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLanguage() {
        // Initialize the database
        languageRepository.save(language).block();

        int databaseSizeBeforeDelete = languageRepository.findAll().collectList().block().size();

        // Delete the language
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, language.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Language> languageList = languageRepository.findAll().collectList().block();
        assertThat(languageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
