package com.giocosmiano.dvdrental.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.giocosmiano.dvdrental.domain.Staff} entity.
 */
public class StaffDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer staffId;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String firstName;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String lastName;

    @Size(max = 255)
    private String email;

    @NotNull(message = "must not be null")
    private Integer storeId;

    @NotNull(message = "must not be null")
    private Boolean active;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String username;

    @Size(max = 255)
    private String password;

    @NotNull(message = "must not be null")
    private Instant lastUpdate;

    @Lob
    private byte[] picture;

    private String pictureContentType;
    private AddressDTO address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getPictureContentType() {
        return pictureContentType;
    }

    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
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
        if (!(o instanceof StaffDTO)) {
            return false;
        }

        StaffDTO staffDTO = (StaffDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, staffDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StaffDTO{" +
            "id=" + getId() +
            ", staffId=" + getStaffId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", storeId=" + getStoreId() +
            ", active='" + getActive() + "'" +
            ", username='" + getUsername() + "'" +
            ", password='" + getPassword() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", picture='" + getPicture() + "'" +
            ", address=" + getAddress() +
            "}";
    }
}
