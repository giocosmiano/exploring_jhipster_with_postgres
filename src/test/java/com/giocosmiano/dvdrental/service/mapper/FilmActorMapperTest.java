package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilmActorMapperTest {

    private FilmActorMapper filmActorMapper;

    @BeforeEach
    public void setUp() {
        filmActorMapper = new FilmActorMapperImpl();
    }
}
