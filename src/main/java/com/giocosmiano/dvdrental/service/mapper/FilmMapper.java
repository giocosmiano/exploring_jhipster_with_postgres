package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Film;
import com.giocosmiano.dvdrental.service.dto.FilmDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Film} and its DTO {@link FilmDTO}.
 */
@Mapper(componentModel = "spring", uses = { LanguageMapper.class })
public interface FilmMapper extends EntityMapper<FilmDTO, Film> {
    @Mapping(target = "language", source = "language", qualifiedByName = "id")
    FilmDTO toDto(Film s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FilmDTO toDtoId(Film film);
}
