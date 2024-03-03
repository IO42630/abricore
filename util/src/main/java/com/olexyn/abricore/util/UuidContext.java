package com.olexyn.abricore.util;

import com.olexyn.abricore.util.exception.MissingException;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages Instances.
 */
@Service
public class UuidContext {

    private final ConfigurableApplicationContext ctx;

    private final Map<Class<?>, Map<UUID, Object>> mapUuid = new HashMap<>();

    @Autowired
    public UuidContext(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }


    /**
     * Service.class -> UUID -> Service
     */
    @Synchronized
    public synchronized <T> T getBean(Class<T> aClass, UUID uuid) {
        mapUuid.computeIfAbsent(aClass, k -> new HashMap<>());
        Object obj = null;
        try {
            obj = mapUuid.get(aClass).get(uuid);
            if (obj == null) {
                obj = ctx.getBean(aClass);
                mapUuid.get(aClass).put(uuid, obj);
            }
        } catch (Exception e) {
            throw new MissingException("Could not get uuid bean." + e.getMessage());
        }
        return aClass.cast(obj);
    }

    @Synchronized
    public void removeBean(Class<?> aClass, UUID uuid) {
        mapUuid.get(aClass).remove(uuid);
    }

}
