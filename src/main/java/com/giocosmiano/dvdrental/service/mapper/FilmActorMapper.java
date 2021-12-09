package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.FilmActor;
import com.giocosmiano.dvdrental.service.dto.FilmActorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FilmActor} and its DTO {@link FilmActorDTO}.
 */
@Mapper(componentModel = "spring", uses = { ActorMapper.class, FilmMapper.class })
public interface FilmActorMapper extends EntityMapper<FilmActorDTO, FilmActor> {
    @Mapping(target = "actor", source = "actor", qualifiedByName = "id")
    @Mapping(target = "film", source = "film", qualifiedByName = "id")
    FilmActorDTO toDto(FilmActor s);
}
