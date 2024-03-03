package com.olexyn.abricore.store.repo;

import com.olexyn.abricore.model.data.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TradeRepo extends JpaRepository<TradeEntity, Long> {

    @Transactional(readOnly = true)
    TradeEntity findByBuyId(String buyId);

}
