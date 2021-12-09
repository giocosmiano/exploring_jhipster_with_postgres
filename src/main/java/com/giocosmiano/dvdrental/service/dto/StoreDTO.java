package com.giocosmiano.dvdrental.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.giocosmiano.dvdrental.domain.Store} entity.
 */
public class StoreDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer storeId;

    @NotNull(message = "must not be null")
    private Instant lastUpdate;

    private StaffDTO managerStaff;

    private AddressDTO address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public StaffDTO getManagerStaff() {
        return managerStaff;
    }

    public void setManagerStaff(StaffDTO managerStaff) {
        this.managerStaff = managerStaff;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoreDTO)) {
            return false;
        }

        StoreDTO storeDTO = (StoreDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoreDTO{" +
            "id=" + getId() +
            ", storeId=" + getStoreId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", managerStaff=" + getManagerStaff() +
            ", address=" + getAddress() +
            "}";
    }
}
