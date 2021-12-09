package com.giocosmiano.dvdrental.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.giocosmiano.dvdrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FilmActorDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilmActorDTO.class);
        FilmActorDTO filmActorDTO1 = new FilmActorDTO();
        filmActorDTO1.setId(1L);
        FilmActorDTO filmActorDTO2 = new FilmActorDTO();
        assertThat(filmActorDTO1).isNotEqualTo(filmActorDTO2);
        filmActorDTO2.setId(filmActorDTO1.getId());
        assertThat(filmActorDTO1).isEqualTo(filmActorDTO2);
        filmActorDTO2.setId(2L);
        assertThat(filmActorDTO1).isNotEqualTo(filmActorDTO2);
        filmActorDTO1.setId(null);
        assertThat(filmActorDTO1).isNotEqualTo(filmActorDTO2);
    }
}
