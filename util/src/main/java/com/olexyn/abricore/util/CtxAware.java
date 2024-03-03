package com.olexyn.abricore.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

/**
 * Provides access to the Spring Context.
 * UUID is a random placeholder.
 */
public abstract class CtxAware {

    @Getter
    private final ConfigurableApplicationContext ctx;

    @Getter
    @Setter
    private UUID uuid = null;

    protected CtxAware(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
        setUuid(UUID.randomUUID());
    }

    /**
     * Get the bean from the UuidContext.
     * If a new bean is created, or an existing bean is returned,
     * is determined by the annotations of the bean itself.
     */
    public <T> T bean(Class<T> clazz) {
        return ctx.getBean(UuidContext.class)
            .getBean(clazz, getUuid());
    }

}
