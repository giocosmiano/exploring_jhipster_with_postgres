package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Customer;
import com.giocosmiano.dvdrental.domain.Inventory;
import com.giocosmiano.dvdrental.domain.Rental;
import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.repository.RentalRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.RentalDTO;
import com.giocosmiano.dvdrental.service.mapper.RentalMapper;
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
 * Integration tests for the {@link RentalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class RentalResourceIT {

    private static final Integer DEFAULT_RENTAL_ID = 1;
    private static final Integer UPDATED_RENTAL_ID = 2;

    private static final Instant DEFAULT_RENTAL_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RENTAL_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_RETURN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RETURN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/rentals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalMapper rentalMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Rental rental;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createEntity(EntityManager em) {
        Rental rental = new Rental()
            .rentalId(DEFAULT_RENTAL_ID)
            .rentalDate(DEFAULT_RENTAL_DATE)
            .returnDate(DEFAULT_RETURN_DATE)
            .lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        Inventory inventory;
        inventory = em.insert(InventoryResourceIT.createEntity(em)).block();
        rental.setInventory(inventory);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createEntity(em)).block();
        rental.setCustomer(customer);
        // Add required entity
        Staff staff;
        staff = em.insert(StaffResourceIT.createEntity(em)).block();
        rental.setStaff(staff);
        return rental;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rental createUpdatedEntity(EntityManager em) {
        Rental rental = new Rental()
            .rentalId(UPDATED_RENTAL_ID)
            .rentalDate(UPDATED_RENTAL_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        Inventory inventory;
        inventory = em.insert(InventoryResourceIT.createUpdatedEntity(em)).block();
        rental.setInventory(inventory);
        // Add required entity
        Customer customer;
        customer = em.insert(CustomerResourceIT.createUpdatedEntity(em)).block();
        rental.setCustomer(customer);
        // Add required entity
        Staff staff;
        staff = em.insert(StaffResourceIT.createUpdatedEntity(em)).block();
        rental.setStaff(staff);
        return rental;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Rental.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        InventoryResourceIT.deleteEntities(em);
        CustomerResourceIT.deleteEntities(em);
        StaffResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        rental = createEntity(em);
    }

    @Test
    void createRental() throws Exception {
        int databaseSizeBeforeCreate = rentalRepository.findAll().collectList().block().size();
        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate + 1);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getRentalId()).isEqualTo(DEFAULT_RENTAL_ID);
        assertThat(testRental.getRentalDate()).isEqualTo(DEFAULT_RENTAL_DATE);
        assertThat(testRental.getReturnDate()).isEqualTo(DEFAULT_RETURN_DATE);
        assertThat(testRental.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createRentalWithExistingId() throws Exception {
        // Create the Rental with an existing ID
        rental.setId(1L);
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        int databaseSizeBeforeCreate = rentalRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkRentalIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalRepository.findAll().collectList().block().size();
        // set the field null
        rental.setRentalId(null);

        // Create the Rental, which fails.
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRentalDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalRepository.findAll().collectList().block().size();
        // set the field null
        rental.setRentalDate(null);

        // Create the Rental, which fails.
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = rentalRepository.findAll().collectList().block().size();
        // set the field null
        rental.setLastUpdate(null);

        // Create the Rental, which fails.
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRentals() {
        // Initialize the database
        rentalRepository.save(rental).block();

        // Get all the rentalList
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
            .value(hasItem(rental.getId().intValue()))
            .jsonPath("$.[*].rentalId")
            .value(hasItem(DEFAULT_RENTAL_ID))
            .jsonPath("$.[*].rentalDate")
            .value(hasItem(DEFAULT_RENTAL_DATE.toString()))
            .jsonPath("$.[*].returnDate")
            .value(hasItem(DEFAULT_RETURN_DATE.toString()))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getRental() {
        // Initialize the database
        rentalRepository.save(rental).block();

        // Get the rental
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, rental.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(rental.getId().intValue()))
            .jsonPath("$.rentalId")
            .value(is(DEFAULT_RENTAL_ID))
            .jsonPath("$.rentalDate")
            .value(is(DEFAULT_RENTAL_DATE.toString()))
            .jsonPath("$.returnDate")
            .value(is(DEFAULT_RETURN_DATE.toString()))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingRental() {
        // Get the rental
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRental() throws Exception {
        // Initialize the database
        rentalRepository.save(rental).block();

        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();

        // Update the rental
        Rental updatedRental = rentalRepository.findById(rental.getId()).block();
        updatedRental
            .rentalId(UPDATED_RENTAL_ID)
            .rentalDate(UPDATED_RENTAL_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .lastUpdate(UPDATED_LAST_UPDATE);
        RentalDTO rentalDTO = rentalMapper.toDto(updatedRental);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getRentalId()).isEqualTo(UPDATED_RENTAL_ID);
        assertThat(testRental.getRentalDate()).isEqualTo(UPDATED_RENTAL_DATE);
        assertThat(testRental.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testRental.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        rentalRepository.save(rental).block();

        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental.rentalDate(UPDATED_RENTAL_DATE).returnDate(UPDATED_RETURN_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getRentalId()).isEqualTo(DEFAULT_RENTAL_ID);
        assertThat(testRental.getRentalDate()).isEqualTo(UPDATED_RENTAL_DATE);
        assertThat(testRental.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testRental.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void fullUpdateRentalWithPatch() throws Exception {
        // Initialize the database
        rentalRepository.save(rental).block();

        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();

        // Update the rental using partial update
        Rental partialUpdatedRental = new Rental();
        partialUpdatedRental.setId(rental.getId());

        partialUpdatedRental
            .rentalId(UPDATED_RENTAL_ID)
            .rentalDate(UPDATED_RENTAL_DATE)
            .returnDate(UPDATED_RETURN_DATE)
            .lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRental.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRental))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
        Rental testRental = rentalList.get(rentalList.size() - 1);
        assertThat(testRental.getRentalId()).isEqualTo(UPDATED_RENTAL_ID);
        assertThat(testRental.getRentalDate()).isEqualTo(UPDATED_RENTAL_DATE);
        assertThat(testRental.getReturnDate()).isEqualTo(UPDATED_RETURN_DATE);
        assertThat(testRental.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, rentalDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRental() throws Exception {
        int databaseSizeBeforeUpdate = rentalRepository.findAll().collectList().block().size();
        rental.setId(count.incrementAndGet());

        // Create the Rental
        RentalDTO rentalDTO = rentalMapper.toDto(rental);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(rentalDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Rental in the database
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRental() {
        // Initialize the database
        rentalRepository.save(rental).block();

        int databaseSizeBeforeDelete = rentalRepository.findAll().collectList().block().size();

        // Delete the rental
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, rental.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Rental> rentalList = rentalRepository.findAll().collectList().block();
        assertThat(rentalList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
