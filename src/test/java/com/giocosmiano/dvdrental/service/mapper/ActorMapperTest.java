package com.giocosmiano.dvdrental.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActorMapperTest {

    private ActorMapper actorMapper;

    @BeforeEach
    public void setUp() {
        actorMapper = new ActorMapperImpl();
    }
}
