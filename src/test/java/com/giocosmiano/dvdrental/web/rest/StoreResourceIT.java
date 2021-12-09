package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Address;
import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.domain.Store;
import com.giocosmiano.dvdrental.repository.StoreRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.StoreDTO;
import com.giocosmiano.dvdrental.service.mapper.StoreMapper;
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
 * Integration tests for the {@link StoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class StoreResourceIT {

    private static final Integer DEFAULT_STORE_ID = 1;
    private static final Integer UPDATED_STORE_ID = 2;

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Store store;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createEntity(EntityManager em) {
        Store store = new Store().storeId(DEFAULT_STORE_ID).lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        Staff staff;
        staff = em.insert(StaffResourceIT.createEntity(em)).block();
        store.setManagerStaff(staff);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createEntity(em)).block();
        store.setAddress(address);
        return store;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Store createUpdatedEntity(EntityManager em) {
        Store store = new Store().storeId(UPDATED_STORE_ID).lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        Staff staff;
        staff = em.insert(StaffResourceIT.createUpdatedEntity(em)).block();
        store.setManagerStaff(staff);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createUpdatedEntity(em)).block();
        store.setAddress(address);
        return store;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Store.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        StaffResourceIT.deleteEntities(em);
        AddressResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        store = createEntity(em);
    }

    @Test
    void createStore() throws Exception {
        int databaseSizeBeforeCreate = storeRepository.findAll().collectList().block().size();
        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate + 1);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getStoreId()).isEqualTo(DEFAULT_STORE_ID);
        assertThat(testStore.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createStoreWithExistingId() throws Exception {
        // Create the Store with an existing ID
        store.setId(1L);
        StoreDTO storeDTO = storeMapper.toDto(store);

        int databaseSizeBeforeCreate = storeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkStoreIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().collectList().block().size();
        // set the field null
        store.setStoreId(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = storeRepository.findAll().collectList().block().size();
        // set the field null
        store.setLastUpdate(null);

        // Create the Store, which fails.
        StoreDTO storeDTO = storeMapper.toDto(store);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllStores() {
        // Initialize the database
        storeRepository.save(store).block();

        // Get all the storeList
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
            .value(hasItem(store.getId().intValue()))
            .jsonPath("$.[*].storeId")
            .value(hasItem(DEFAULT_STORE_ID))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getStore() {
        // Initialize the database
        storeRepository.save(store).block();

        // Get the store
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, store.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(store.getId().intValue()))
            .jsonPath("$.storeId")
            .value(is(DEFAULT_STORE_ID))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingStore() {
        // Get the store
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStore() throws Exception {
        // Initialize the database
        storeRepository.save(store).block();

        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();

        // Update the store
        Store updatedStore = storeRepository.findById(store.getId()).block();
        updatedStore.storeId(UPDATED_STORE_ID).lastUpdate(UPDATED_LAST_UPDATE);
        StoreDTO storeDTO = storeMapper.toDto(updatedStore);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, storeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testStore.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, storeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStoreWithPatch() throws Exception {
        // Initialize the database
        storeRepository.save(store).block();

        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();

        // Update the store using partial update
        Store partialUpdatedStore = new Store();
        partialUpdatedStore.setId(store.getId());

        partialUpdatedStore.lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getStoreId()).isEqualTo(DEFAULT_STORE_ID);
        assertThat(testStore.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void fullUpdateStoreWithPatch() throws Exception {
        // Initialize the database
        storeRepository.save(store).block();

        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();

        // Update the store using partial update
        Store partialUpdatedStore = new Store();
        partialUpdatedStore.setId(store.getId());

        partialUpdatedStore.storeId(UPDATED_STORE_ID).lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
        Store testStore = storeList.get(storeList.size() - 1);
        assertThat(testStore.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testStore.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, storeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStore() throws Exception {
        int databaseSizeBeforeUpdate = storeRepository.findAll().collectList().block().size();
        store.setId(count.incrementAndGet());

        // Create the Store
        StoreDTO storeDTO = storeMapper.toDto(store);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(storeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Store in the database
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStore() {
        // Initialize the database
        storeRepository.save(store).block();

        int databaseSizeBeforeDelete = storeRepository.findAll().collectList().block().size();

        // Delete the store
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, store.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Store> storeList = storeRepository.findAll().collectList().block();
        assertThat(storeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
