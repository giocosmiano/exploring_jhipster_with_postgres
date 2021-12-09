package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilmMapperTest {

    private FilmMapper filmMapper;

    @BeforeEach
    public void setUp() {
        filmMapper = new FilmMapperImpl();
    }
}
