package com.giocosmiano.dvdrental.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.giocosmiano.dvdrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FilmCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilmCategory.class);
        FilmCategory filmCategory1 = new FilmCategory();
        filmCategory1.setId(1L);
        FilmCategory filmCategory2 = new FilmCategory();
        filmCategory2.setId(filmCategory1.getId());
        assertThat(filmCategory1).isEqualTo(filmCategory2);
        filmCategory2.setId(2L);
        assertThat(filmCategory1).isNotEqualTo(filmCategory2);
        filmCategory1.setId(null);
        assertThat(filmCategory1).isNotEqualTo(filmCategory2);
    }
}
