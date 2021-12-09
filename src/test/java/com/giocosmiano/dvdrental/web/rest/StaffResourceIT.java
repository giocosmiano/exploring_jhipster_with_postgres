package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Address;
import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.repository.StaffRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.StaffDTO;
import com.giocosmiano.dvdrental.service.mapper.StaffMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link StaffResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class StaffResourceIT {

    private static final Integer DEFAULT_STAFF_ID = 1;
    private static final Integer UPDATED_STAFF_ID = 2;

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Integer DEFAULT_STORE_ID = 1;
    private static final Integer UPDATED_STORE_ID = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final byte[] DEFAULT_PICTURE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PICTURE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PICTURE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PICTURE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/staff";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Staff staff;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createEntity(EntityManager em) {
        Staff staff = new Staff()
            .staffId(DEFAULT_STAFF_ID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .storeId(DEFAULT_STORE_ID)
            .active(DEFAULT_ACTIVE)
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .lastUpdate(DEFAULT_LAST_UPDATE)
            .picture(DEFAULT_PICTURE)
            .pictureContentType(DEFAULT_PICTURE_CONTENT_TYPE);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createEntity(em)).block();
        staff.setAddress(address);
        return staff;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Staff createUpdatedEntity(EntityManager em) {
        Staff staff = new Staff()
            .staffId(UPDATED_STAFF_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .storeId(UPDATED_STORE_ID)
            .active(UPDATED_ACTIVE)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);
        // Add required entity
        Address address;
        address = em.insert(AddressResourceIT.createUpdatedEntity(em)).block();
        staff.setAddress(address);
        return staff;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Staff.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        AddressResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        staff = createEntity(em);
    }

    @Test
    void createStaff() throws Exception {
        int databaseSizeBeforeCreate = staffRepository.findAll().collectList().block().size();
        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate + 1);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffId()).isEqualTo(DEFAULT_STAFF_ID);
        assertThat(testStaff.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testStaff.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testStaff.getStoreId()).isEqualTo(DEFAULT_STORE_ID);
        assertThat(testStaff.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testStaff.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testStaff.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testStaff.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
        assertThat(testStaff.getPicture()).isEqualTo(DEFAULT_PICTURE);
        assertThat(testStaff.getPictureContentType()).isEqualTo(DEFAULT_PICTURE_CONTENT_TYPE);
    }

    @Test
    void createStaffWithExistingId() throws Exception {
        // Create the Staff with an existing ID
        staff.setId(1L);
        StaffDTO staffDTO = staffMapper.toDto(staff);

        int databaseSizeBeforeCreate = staffRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkStaffIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setStaffId(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setFirstName(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setLastName(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStoreIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setStoreId(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setActive(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setUsername(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = staffRepository.findAll().collectList().block().size();
        // set the field null
        staff.setLastUpdate(null);

        // Create the Staff, which fails.
        StaffDTO staffDTO = staffMapper.toDto(staff);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllStaff() {
        // Initialize the database
        staffRepository.save(staff).block();

        // Get all the staffList
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
            .value(hasItem(staff.getId().intValue()))
            .jsonPath("$.[*].staffId")
            .value(hasItem(DEFAULT_STAFF_ID))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].storeId")
            .value(hasItem(DEFAULT_STORE_ID))
            .jsonPath("$.[*].active")
            .value(hasItem(DEFAULT_ACTIVE.booleanValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()))
            .jsonPath("$.[*].pictureContentType")
            .value(hasItem(DEFAULT_PICTURE_CONTENT_TYPE))
            .jsonPath("$.[*].picture")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURE)));
    }

    @Test
    void getStaff() {
        // Initialize the database
        staffRepository.save(staff).block();

        // Get the staff
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, staff.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(staff.getId().intValue()))
            .jsonPath("$.staffId")
            .value(is(DEFAULT_STAFF_ID))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.storeId")
            .value(is(DEFAULT_STORE_ID))
            .jsonPath("$.active")
            .value(is(DEFAULT_ACTIVE.booleanValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()))
            .jsonPath("$.pictureContentType")
            .value(is(DEFAULT_PICTURE_CONTENT_TYPE))
            .jsonPath("$.picture")
            .value(is(Base64Utils.encodeToString(DEFAULT_PICTURE)));
    }

    @Test
    void getNonExistingStaff() {
        // Get the staff
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStaff() throws Exception {
        // Initialize the database
        staffRepository.save(staff).block();

        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();

        // Update the staff
        Staff updatedStaff = staffRepository.findById(staff.getId()).block();
        updatedStaff
            .staffId(UPDATED_STAFF_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .storeId(UPDATED_STORE_ID)
            .active(UPDATED_ACTIVE)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);
        StaffDTO staffDTO = staffMapper.toDto(updatedStaff);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, staffDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffId()).isEqualTo(UPDATED_STAFF_ID);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testStaff.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStaff.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testStaff.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testStaff.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testStaff.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testStaff.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testStaff.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testStaff.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
    }

    @Test
    void putNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, staffDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.save(staff).block();

        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .storeId(UPDATED_STORE_ID)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffId()).isEqualTo(DEFAULT_STAFF_ID);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testStaff.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testStaff.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testStaff.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testStaff.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testStaff.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testStaff.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testStaff.getPicture()).isEqualTo(DEFAULT_PICTURE);
        assertThat(testStaff.getPictureContentType()).isEqualTo(DEFAULT_PICTURE_CONTENT_TYPE);
    }

    @Test
    void fullUpdateStaffWithPatch() throws Exception {
        // Initialize the database
        staffRepository.save(staff).block();

        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();

        // Update the staff using partial update
        Staff partialUpdatedStaff = new Staff();
        partialUpdatedStaff.setId(staff.getId());

        partialUpdatedStaff
            .staffId(UPDATED_STAFF_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .storeId(UPDATED_STORE_ID)
            .active(UPDATED_ACTIVE)
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .lastUpdate(UPDATED_LAST_UPDATE)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStaff.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStaff))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
        Staff testStaff = staffList.get(staffList.size() - 1);
        assertThat(testStaff.getStaffId()).isEqualTo(UPDATED_STAFF_ID);
        assertThat(testStaff.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testStaff.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testStaff.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testStaff.getStoreId()).isEqualTo(UPDATED_STORE_ID);
        assertThat(testStaff.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testStaff.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testStaff.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testStaff.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testStaff.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testStaff.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, staffDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStaff() throws Exception {
        int databaseSizeBeforeUpdate = staffRepository.findAll().collectList().block().size();
        staff.setId(count.incrementAndGet());

        // Create the Staff
        StaffDTO staffDTO = staffMapper.toDto(staff);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(staffDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Staff in the database
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStaff() {
        // Initialize the database
        staffRepository.save(staff).block();

        int databaseSizeBeforeDelete = staffRepository.findAll().collectList().block().size();

        // Delete the staff
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, staff.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Staff> staffList = staffRepository.findAll().collectList().block();
        assertThat(staffList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
