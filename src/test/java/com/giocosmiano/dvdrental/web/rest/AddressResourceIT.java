package com.giocosmiano.dvdrental.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.giocosmiano.dvdrental.IntegrationTest;
import com.giocosmiano.dvdrental.domain.Address;
import com.giocosmiano.dvdrental.domain.City;
import com.giocosmiano.dvdrental.repository.AddressRepository;
import com.giocosmiano.dvdrental.service.EntityManager;
import com.giocosmiano.dvdrental.service.dto.AddressDTO;
import com.giocosmiano.dvdrental.service.mapper.AddressMapper;
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
 * Integration tests for the {@link AddressResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class AddressResourceIT {

    private static final Integer DEFAULT_ADDRESS_ID = 1;
    private static final Integer UPDATED_ADDRESS_ID = 2;

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_2 = "BBBBBBBBBB";

    private static final String DEFAULT_DISTRICT = "AAAAAAAAAA";
    private static final String UPDATED_DISTRICT = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_UPDATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/addresses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Address address;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity(EntityManager em) {
        Address address = new Address()
            .addressId(DEFAULT_ADDRESS_ID)
            .address(DEFAULT_ADDRESS)
            .address2(DEFAULT_ADDRESS_2)
            .district(DEFAULT_DISTRICT)
            .postalCode(DEFAULT_POSTAL_CODE)
            .phone(DEFAULT_PHONE)
            .lastUpdate(DEFAULT_LAST_UPDATE);
        // Add required entity
        City city;
        city = em.insert(CityResourceIT.createEntity(em)).block();
        address.setCity(city);
        return address;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createUpdatedEntity(EntityManager em) {
        Address address = new Address()
            .addressId(UPDATED_ADDRESS_ID)
            .address(UPDATED_ADDRESS)
            .address2(UPDATED_ADDRESS_2)
            .district(UPDATED_DISTRICT)
            .postalCode(UPDATED_POSTAL_CODE)
            .phone(UPDATED_PHONE)
            .lastUpdate(UPDATED_LAST_UPDATE);
        // Add required entity
        City city;
        city = em.insert(CityResourceIT.createUpdatedEntity(em)).block();
        address.setCity(city);
        return address;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Address.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CityResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        address = createEntity(em);
    }

    @Test
    void createAddress() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().collectList().block().size();
        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate + 1);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(DEFAULT_ADDRESS_ID);
        assertThat(testAddress.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testAddress.getAddress2()).isEqualTo(DEFAULT_ADDRESS_2);
        assertThat(testAddress.getDistrict()).isEqualTo(DEFAULT_DISTRICT);
        assertThat(testAddress.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testAddress.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAddress.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
    }

    @Test
    void createAddressWithExistingId() throws Exception {
        // Create the Address with an existing ID
        address.setId(1L);
        AddressDTO addressDTO = addressMapper.toDto(address);

        int databaseSizeBeforeCreate = addressRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkAddressIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setAddressId(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setAddress(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkDistrictIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setDistrict(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setPhone(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = addressRepository.findAll().collectList().block().size();
        // set the field null
        address.setLastUpdate(null);

        // Create the Address, which fails.
        AddressDTO addressDTO = addressMapper.toDto(address);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAddresses() {
        // Initialize the database
        addressRepository.save(address).block();

        // Get all the addressList
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
            .value(hasItem(address.getId().intValue()))
            .jsonPath("$.[*].addressId")
            .value(hasItem(DEFAULT_ADDRESS_ID))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].address2")
            .value(hasItem(DEFAULT_ADDRESS_2))
            .jsonPath("$.[*].district")
            .value(hasItem(DEFAULT_DISTRICT))
            .jsonPath("$.[*].postalCode")
            .value(hasItem(DEFAULT_POSTAL_CODE))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].lastUpdate")
            .value(hasItem(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getAddress() {
        // Initialize the database
        addressRepository.save(address).block();

        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(address.getId().intValue()))
            .jsonPath("$.addressId")
            .value(is(DEFAULT_ADDRESS_ID))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.address2")
            .value(is(DEFAULT_ADDRESS_2))
            .jsonPath("$.district")
            .value(is(DEFAULT_DISTRICT))
            .jsonPath("$.postalCode")
            .value(is(DEFAULT_POSTAL_CODE))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.lastUpdate")
            .value(is(DEFAULT_LAST_UPDATE.toString()));
    }

    @Test
    void getNonExistingAddress() {
        // Get the address
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAddress() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address
        Address updatedAddress = addressRepository.findById(address.getId()).block();
        updatedAddress
            .addressId(UPDATED_ADDRESS_ID)
            .address(UPDATED_ADDRESS)
            .address2(UPDATED_ADDRESS_2)
            .district(UPDATED_DISTRICT)
            .postalCode(UPDATED_POSTAL_CODE)
            .phone(UPDATED_PHONE)
            .lastUpdate(UPDATED_LAST_UPDATE);
        AddressDTO addressDTO = addressMapper.toDto(updatedAddress);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(UPDATED_ADDRESS_ID);
        assertThat(testAddress.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAddress.getAddress2()).isEqualTo(UPDATED_ADDRESS_2);
        assertThat(testAddress.getDistrict()).isEqualTo(UPDATED_DISTRICT);
        assertThat(testAddress.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAddress.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAddress.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void putNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .address(UPDATED_ADDRESS)
            .address2(UPDATED_ADDRESS_2)
            .district(UPDATED_DISTRICT)
            .postalCode(UPDATED_POSTAL_CODE)
            .lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(DEFAULT_ADDRESS_ID);
        assertThat(testAddress.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAddress.getAddress2()).isEqualTo(UPDATED_ADDRESS_2);
        assertThat(testAddress.getDistrict()).isEqualTo(UPDATED_DISTRICT);
        assertThat(testAddress.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAddress.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testAddress.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void fullUpdateAddressWithPatch() throws Exception {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();

        // Update the address using partial update
        Address partialUpdatedAddress = new Address();
        partialUpdatedAddress.setId(address.getId());

        partialUpdatedAddress
            .addressId(UPDATED_ADDRESS_ID)
            .address(UPDATED_ADDRESS)
            .address2(UPDATED_ADDRESS_2)
            .district(UPDATED_DISTRICT)
            .postalCode(UPDATED_POSTAL_CODE)
            .phone(UPDATED_PHONE)
            .lastUpdate(UPDATED_LAST_UPDATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAddress.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAddress))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressId()).isEqualTo(UPDATED_ADDRESS_ID);
        assertThat(testAddress.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testAddress.getAddress2()).isEqualTo(UPDATED_ADDRESS_2);
        assertThat(testAddress.getDistrict()).isEqualTo(UPDATED_DISTRICT);
        assertThat(testAddress.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testAddress.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testAddress.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
    }

    @Test
    void patchNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, addressDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().collectList().block().size();
        address.setId(count.incrementAndGet());

        // Create the Address
        AddressDTO addressDTO = addressMapper.toDto(address);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(addressDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAddress() {
        // Initialize the database
        addressRepository.save(address).block();

        int databaseSizeBeforeDelete = addressRepository.findAll().collectList().block().size();

        // Delete the address
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, address.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Address> addressList = addressRepository.findAll().collectList().block();
        assertThat(addressList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
