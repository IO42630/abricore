package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.VectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VectorRepo extends JpaRepository<VectorEntity, Long> {

    List<VectorEntity> findTop640ByOrderByRatingDesc();

}
