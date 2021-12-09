package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.City;
import com.giocosmiano.dvdrental.service.dto.CityDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link City} and its DTO {@link CityDTO}.
 */
@Mapper(componentModel = "spring", uses = { CountryMapper.class })
public interface CityMapper extends EntityMapper<CityDTO, City> {
    @Mapping(target = "country", source = "country", qualifiedByName = "id")
    CityDTO toDto(City s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CityDTO toDtoId(City city);
}
