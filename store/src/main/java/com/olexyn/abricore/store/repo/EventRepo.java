package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.EventEntity;
import com.olexyn.abricore.model.data.SnapshotDistanceEntity;
import com.olexyn.abricore.util.enums.SnapshotDistanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<EventEntity, Long> {

}
