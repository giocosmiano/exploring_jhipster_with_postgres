package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Inventory;
import com.giocosmiano.dvdrental.service.dto.InventoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { FilmMapper.class })
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {
    @Mapping(target = "film", source = "film", qualifiedByName = "id")
    InventoryDTO toDto(Inventory s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InventoryDTO toDtoId(Inventory inventory);
}
