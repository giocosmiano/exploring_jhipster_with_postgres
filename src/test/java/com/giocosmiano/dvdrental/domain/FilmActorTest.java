package com.giocosmiano.dvdrental.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.giocosmiano.dvdrental.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FilmActorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FilmActor.class);
        FilmActor filmActor1 = new FilmActor();
        filmActor1.setId(1L);
        FilmActor filmActor2 = new FilmActor();
        filmActor2.setId(filmActor1.getId());
        assertThat(filmActor1).isEqualTo(filmActor2);
        filmActor2.setId(2L);
        assertThat(filmActor1).isNotEqualTo(filmActor2);
        filmActor1.setId(null);
        assertThat(filmActor1).isNotEqualTo(filmActor2);
    }
}
