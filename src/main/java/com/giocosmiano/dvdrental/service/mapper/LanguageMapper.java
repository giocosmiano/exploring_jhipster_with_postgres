package com.giocosmiano.dvdrental.service.mapper;

import com.giocosmiano.dvdrental.domain.Language;
import com.giocosmiano.dvdrental.service.dto.LanguageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Language} and its DTO {@link LanguageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LanguageMapper extends EntityMapper<LanguageDTO, Language> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LanguageDTO toDtoId(Language language);
}
