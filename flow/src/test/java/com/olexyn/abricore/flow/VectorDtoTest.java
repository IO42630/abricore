package com.olexyn.abricore.flow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.olexyn.abricore.flow.strategy.templates.VectorTemplates;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootTest(classes = {VectorTemplates.class})
public class VectorDtoTest {

    ConfigurableApplicationContext ctx;

    @Before
    public void init() {
        ctx = SpringApplication.run(VectorTemplates.class);
    }


    @Test
    public void jsonTest() throws JsonProcessingException {
    }
}
