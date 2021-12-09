package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Store;
import com.giocosmiano.dvdrental.service.dto.StoreDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Store} and its DTO {@link StoreDTO}.
 */
@Mapper(componentModel = "spring", uses = { StaffMapper.class, AddressMapper.class })
public interface StoreMapper extends EntityMapper<StoreDTO, Store> {
    @Mapping(target = "managerStaff", source = "managerStaff", qualifiedByName = "id")
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    StoreDTO toDto(Store s);
}
