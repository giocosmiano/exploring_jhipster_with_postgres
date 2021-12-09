package com.giocosmiano.dvdrental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.giocosmiano.dvdrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FilmCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilmCategoryDTO.class);
        FilmCategoryDTO filmCategoryDTO1 = new FilmCategoryDTO();
        filmCategoryDTO1.setId(1L);
        FilmCategoryDTO filmCategoryDTO2 = new FilmCategoryDTO();
        assertThat(filmCategoryDTO1).isNotEqualTo(filmCategoryDTO2);
        filmCategoryDTO2.setId(filmCategoryDTO1.getId());
        assertThat(filmCategoryDTO1).isEqualTo(filmCategoryDTO2);
        filmCategoryDTO2.setId(2L);
        assertThat(filmCategoryDTO1).isNotEqualTo(filmCategoryDTO2);
        filmCategoryDTO1.setId(null);
        assertThat(filmCategoryDTO1).isNotEqualTo(filmCategoryDTO2);
    }
}
