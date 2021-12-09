package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Actor;
import com.giocosmiano.dvdrental.service.dto.ActorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Actor} and its DTO {@link ActorDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ActorMapper extends EntityMapper<ActorDTO, Actor> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ActorDTO toDtoId(Actor actor);
}
