package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.FilmCategory;
import com.giocosmiano.dvdrental.service.dto.FilmCategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FilmCategory} and its DTO {@link FilmCategoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { FilmMapper.class, CategoryMapper.class })
public interface FilmCategoryMapper extends EntityMapper<FilmCategoryDTO, FilmCategory> {
    @Mapping(target = "film", source = "film", qualifiedByName = "id")
    @Mapping(target = "category", source = "category", qualifiedByName = "id")
    FilmCategoryDTO toDto(FilmCategory s);
}
