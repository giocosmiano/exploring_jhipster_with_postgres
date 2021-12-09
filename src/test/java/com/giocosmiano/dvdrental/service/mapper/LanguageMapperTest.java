package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LanguageMapperTest {

    private LanguageMapper languageMapper;

    @BeforeEach
    public void setUp() {
        languageMapper = new LanguageMapperImpl();
    }
}
