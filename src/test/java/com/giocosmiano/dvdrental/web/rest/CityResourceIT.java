package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.City;
import com.giocosmiano.dvdrental.domain.Country;
import com.giocosmiano.dvdrental.repository.CityRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.CityDTO;
import com.giocosmiano.dvdrental.service.mapper.CityMapper;
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
 * Integration tests for the {@link CityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CityResourceIT {

    private static final Integer DEFAULT_CITY_ID = 1;
    private static final Integer UPDATED_CITY_ID = 2;

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private City city;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createEntity(EntityManager em) {
        City city = new City().cityId(DEFAULT_CITY_ID).city(DEFAULT_CITY).lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        Country country;
        country = em.insert(CountryResourceIT.createEntity(em)).block();
        city.setCountry(country);
        return city;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static City createUpdatedEntity(EntityManager em) {
        City city = new City().cityId(UPDATED_CITY_ID).city(UPDATED_CITY).lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        Country country;
        country = em.insert(CountryResourceIT.createUpdatedEntity(em)).block();
        city.setCountry(country);
        return city;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(City.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CountryResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        city = createEntity(em);
    }

    @Test
    void createCity() throws Exception {
        int databaseSizeBeforeCreate = cityRepository.findAll().collectList().block().size();
        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate + 1);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getCityId()).isEqualTo(DEFAULT_CITY_ID);
        assertThat(testCity.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCity.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createCityWithExistingId() throws Exception {
        // Create the City with an existing ID
        city.setId(1L);
        CityDTO cityDTO = cityMapper.toDto(city);

        int databaseSizeBeforeCreate = cityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkCityIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = cityRepository.findAll().collectList().block().size();
        // set the field null
        city.setCityId(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = cityRepository.findAll().collectList().block().size();
        // set the field null
        city.setCity(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = cityRepository.findAll().collectList().block().size();
        // set the field null
        city.setLastUpdate(null);

        // Create the City, which fails.
        CityDTO cityDTO = cityMapper.toDto(city);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCities() {
        // Initialize the database
        cityRepository.save(city).block();

        // Get all the cityList
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
            .value(hasItem(city.getId().intValue()))
            .jsonPath("$.[*].cityId")
            .value(hasItem(DEFAULT_CITY_ID))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getCity() {
        // Initialize the database
        cityRepository.save(city).block();

        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(city.getId().intValue()))
            .jsonPath("$.cityId")
            .value(is(DEFAULT_CITY_ID))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingCity() {
        // Get the city
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCity() throws Exception {
        // Initialize the database
        cityRepository.save(city).block();

        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();

        // Update the city
        City updatedCity = cityRepository.findById(city.getId()).block();
        updatedCity.cityId(UPDATED_CITY_ID).city(UPDATED_CITY).lastUpdate(UPDATED_LAST_UPDATE);
        CityDTO cityDTO = cityMapper.toDto(updatedCity);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getCityId()).isEqualTo(UPDATED_CITY_ID);
        assertThat(testCity.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCity.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCityWithPatch() throws Exception {
        // Initialize the database
        cityRepository.save(city).block();

        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.city(UPDATED_CITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getCityId()).isEqualTo(DEFAULT_CITY_ID);
        assertThat(testCity.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCity.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void fullUpdateCityWithPatch() throws Exception {
        // Initialize the database
        cityRepository.save(city).block();

        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();

        // Update the city using partial update
        City partialUpdatedCity = new City();
        partialUpdatedCity.setId(city.getId());

        partialUpdatedCity.cityId(UPDATED_CITY_ID).city(UPDATED_CITY).lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCity.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCity))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
        City testCity = cityList.get(cityList.size() - 1);
        assertThat(testCity.getCityId()).isEqualTo(UPDATED_CITY_ID);
        assertThat(testCity.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCity.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cityDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCity() throws Exception {
        int databaseSizeBeforeUpdate = cityRepository.findAll().collectList().block().size();
        city.setId(count.incrementAndGet());

        // Create the City
        CityDTO cityDTO = cityMapper.toDto(city);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cityDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the City in the database
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCity() {
        // Initialize the database
        cityRepository.save(city).block();

        int databaseSizeBeforeDelete = cityRepository.findAll().collectList().block().size();

        // Delete the city
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, city.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<City> cityList = cityRepository.findAll().collectList().block();
        assertThat(cityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
