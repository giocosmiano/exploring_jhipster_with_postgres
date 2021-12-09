package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FilmCategoryMapperTest {

    private FilmCategoryMapper filmCategoryMapper;

    @BeforeEach
    public void setUp() {
        filmCategoryMapper = new FilmCategoryMapperImpl();
    }
}
