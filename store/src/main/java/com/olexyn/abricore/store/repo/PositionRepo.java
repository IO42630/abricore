package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepo extends JpaRepository<PositionEntity, Long> {

}
