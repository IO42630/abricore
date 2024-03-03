package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.SymbolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolRepo extends JpaRepository<SymbolEntity, Long> {

}
