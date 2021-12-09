package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Staff;
import com.giocosmiano.dvdrental.service.dto.StaffDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Staff} and its DTO {@link StaffDTO}.
 */
@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface StaffMapper extends EntityMapper<StaffDTO, Staff> {
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    StaffDTO toDto(Staff s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StaffDTO toDtoId(Staff staff);
}
