package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Rental;
import com.giocosmiano.dvdrental.service.dto.RentalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Rental} and its DTO {@link RentalDTO}.
 */
@Mapper(componentModel = "spring", uses = { InventoryMapper.class, CustomerMapper.class, StaffMapper.class })
public interface RentalMapper extends EntityMapper<RentalDTO, Rental> {
    @Mapping(target = "inventory", source = "inventory", qualifiedByName = "id")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "id")
    @Mapping(target = "staff", source = "staff", qualifiedByName = "id")
    RentalDTO toDto(Rental s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RentalDTO toDtoId(Rental rental);
}
