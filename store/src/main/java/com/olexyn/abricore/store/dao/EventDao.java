package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.EventEntity;
import com.olexyn.abricore.model.data.FrameEntity;
import com.olexyn.abricore.model.runtime.snapshots.FrameDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.EventRepo;
import com.olexyn.abricore.store.repo.FrameRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.enums.FrameType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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
