package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StaffMapperTest {

    private StaffMapper staffMapper;

    @BeforeEach
    public void setUp() {
        staffMapper = new StaffMapperImpl();
    }
}
