package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Customer;
import com.giocosmiano.dvdrental.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring", uses = { AddressMapper.class })
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "address", source = "address", qualifiedByName = "id")
    CustomerDTO toDto(Customer s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoId(Customer customer);
}
