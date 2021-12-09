package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Address;
import com.giocosmiano.dvdrental.service.dto.AddressDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring", uses = { CityMapper.class })
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "city", source = "city", qualifiedByName = "id")
    AddressDTO toDto(Address s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AddressDTO toDtoId(Address address);
}
