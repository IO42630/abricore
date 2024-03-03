package com.olexyn.abricore.store.dao;

import com.olexyn.abricore.model.data.TradeEntity;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.store.Mapper;
import com.olexyn.abricore.store.repo.TradeRepo;
import com.olexyn.abricore.store.runtime.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class TradeDao extends Dao<TradeEntity, TradeDto> implements StringKeyDao<TradeEntity, TradeDto> {

    private final TradeRepo tradeRepo;

    @Autowired
    public TradeDao(
        TradeRepo tradeRepo,
        Mapper mapper,
        AssetService assetService
    ) {
        super(mapper, assetService);
        this.tradeRepo = tradeRepo;
    }

    @Override
    protected JpaRepository<TradeEntity, Long> getRepo() {
        return tradeRepo;
    }

    @Override
    public TradeDto find(String buyId) {
        var entity = tradeRepo.findByBuyId(buyId);
        var mapped = mapper.toTradeDto(entity);
        return postProcess(mapped);
    }


    @Override
    protected TradeDto toDto(TradeEntity entity) {
        return mapper.toTradeDto(entity);
    }

    @Override
    protected TradeEntity toEntity(TradeDto dto) {
        return mapper.toTradeEntity(dto);
    }

}
