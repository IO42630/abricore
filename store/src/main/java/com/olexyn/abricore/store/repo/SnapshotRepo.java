package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
public interface SnapshotRepo extends JpaRepository<SnapshotEntity, Long> {

    @Transactional(readOnly = true)
    SnapshotEntity findByAssetAndInstant(String assetName, Instant instant);

    @Transactional(readOnly = true)
    List<SnapshotEntity> findAllByAsset(String assetName);

    @Transactional(readOnly = true)
    @Query(
        value = "SELECT * FROM t_snapshot lr WHERE ASSET = :assetName AND INSTANT >= :from  AND INSTANT <= :to",
        nativeQuery = true
    )
    List<SnapshotEntity> findSegmentByAsset(
        @Param("assetName") String assetName,
        @Param("from") String from,
        @Param("to") String to
    );

    @Transactional(readOnly = true)
    List<SnapshotEntity> findAllByAssetAndInstantAfterAndInstantBefore(String assetName, Instant from, Instant to);

}
