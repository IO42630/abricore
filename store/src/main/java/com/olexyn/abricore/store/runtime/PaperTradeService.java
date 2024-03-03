package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.store.dao.PositionDao;
import com.olexyn.abricore.store.dao.TradeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Scope("prototype")
@Service
public class PaperTradeService extends ProtoTradeService {


    @Autowired
    public PaperTradeService(
        TradeDao tradeDao,
        PositionDao positionDao
    ) {
        super(tradeDao, positionDao);
        // no need to load anything from Repo.
    }

    @Override
    public synchronized void save() {
        // No need to persist paper trades.
    }

}
