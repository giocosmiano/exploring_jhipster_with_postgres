package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Payment;
import com.giocosmiano.dvdrental.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring", uses = { CustomerMapper.class, StaffMapper.class, RentalMapper.class })
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "id")
    @Mapping(target = "staff", source = "staff", qualifiedByName = "id")
    @Mapping(target = "rental", source = "rental", qualifiedByName = "id")
    PaymentDTO toDto(Payment s);
}
