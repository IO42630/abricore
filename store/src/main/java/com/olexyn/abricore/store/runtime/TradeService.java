package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.store.dao.PositionDao;
import com.olexyn.abricore.store.dao.TradeDao;
import org.springframework.stereotype.Service;

/**
 * ONLY so we don't have to mess with @Qualifier.
 */
@Service
public class TradeService extends ProtoTradeService {

    public TradeService(TradeDao tradeDao, PositionDao positionDao) {
        super(tradeDao, positionDao);
        tradeDao.findDtos()
            .forEach(trade -> getTrades().put(trade.getUuid(), trade));
    }

}
