package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.FrameEntity;
import com.olexyn.abricore.util.enums.FrameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameRepo extends JpaRepository<FrameEntity, Long> {


    List<FrameEntity> findAllByAssetAndFrameType(String asset, FrameType frameType);


}
