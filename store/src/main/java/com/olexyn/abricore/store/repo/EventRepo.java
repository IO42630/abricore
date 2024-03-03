package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.EventEntity;
import com.olexyn.abricore.model.data.FrameEntity;
import com.olexyn.abricore.util.enums.FrameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, Long> {

}
