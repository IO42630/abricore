package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.EventEntity;
import com.olexyn.abricore.store.repo.EventRepo;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventDao {

    private final EventRepo eventRepo;

    @Autowired
    public EventDao(
        EventRepo eventRepo
    ) {
        this.eventRepo = eventRepo;
    }

    public void set(String eventKey, String value) {
        var eventEntity  = eventRepo.findAll()
            .stream()
            .filter(e -> e.getEventKey().equals(eventKey))
            .findFirst()
            .orElse(new EventEntity());
        eventEntity.setEventKey(eventKey);
        eventEntity.setValue(value);
        eventRepo.save(eventEntity);
    }


    public @Nullable String get(String eventKey) {
        return eventRepo.findAll()
            .stream().filter(e -> e.getEventKey().equals(eventKey))
            .map(EventEntity::getValue)
            .findFirst()
            .orElse(null);
    }

    public Instant getInstant(String eventKey) {
        return eventRepo.findAll()
            .stream().filter(e -> e.getEventKey().equals(eventKey))
            .map(EventEntity::getValue)
            .map(Instant::parse)
            .findFirst()
            .orElse(Instant.MIN);
    }

    public boolean getBool(String eventKey) {
        return eventRepo.findAll()
            .stream().filter(e -> e.getEventKey().equals(eventKey))
            .map(EventEntity::getValue)
            .map(Boolean::parseBoolean)
            .findFirst()
            .orElse(false);
    }

}
