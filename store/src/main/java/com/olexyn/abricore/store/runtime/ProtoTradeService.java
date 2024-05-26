package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.model.runtime.PositionDto;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.AssetType;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.store.dao.PositionDao;
import com.olexyn.abricore.store.dao.TradeDao;
import com.olexyn.abricore.util.enums.TradeStatus;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Stream;

import static com.olexyn.abricore.util.enums.TradeStatus.ACTIVE_POS;
import static com.olexyn.abricore.util.enums.TradeStatus.CANCEL_EXECUTED;
import static com.olexyn.abricore.util.enums.TradeStatus.CLOSE_EXECUTED;

@Service
public abstract class ProtoTradeService implements DtoService<TradeDto> {

    private final TradeDao tradeDao;
    private final PositionDao positionDao;

    private final TreeMap<UUID, TradeDto> TRADE_MAP = new TreeMap<>();

    @Autowired
    protected ProtoTradeService(
        TradeDao tradeDao,
        PositionDao positionDao
    ) {
        this.tradeDao = tradeDao;
        this.positionDao = positionDao;
        // delegate loading from Repo to child.
    }

    public TreeMap<UUID, TradeDto> getTrades() {
        return TRADE_MAP;
    }

    private Stream<TradeDto> getTradesStream() {
        return getTrades().values().stream();
    }

    public Stream<TradeDto> getTradesStreamCopy() {
        return (new ArrayList<>(getTrades().values())).parallelStream();
    }


    /**
     * ABC-1
     */
    public synchronized @Nullable TradeDto of(TradeDto candiate) {
        return getTradesStream()
            .filter(t -> t.getAsset().getName().equals(candiate.getAsset().getName()))
            .filter(t -> t.getAmount() == candiate.getAmount())
            .max(Comparator.comparing(TradeDto::getId))
            .orElse(null);
    }


    /**
     * Key is the BuyId of the TradeDto.
     */
    public synchronized @Nullable TradeDto put(TradeDto value) {
        return getTrades().put(value.getUuid(), value);
    }

    @Override
    public synchronized ProtoTradeService update(Set<TradeDto> trades) {
        Set<TradeDto> toAdd = new HashSet<>();
        trades.forEach(trade -> {
            var existingTrade = of(trade);
            if (existingTrade == null) { toAdd.add(trade); } else { existingTrade.mergeFrom(trade); }
        });
        toAdd.forEach(trade -> getTrades().put(trade.getUuid(), trade));
        return this;
    }

    @Override
    public synchronized void save() {
        addPreExistingTrades();
        tradeDao.saveDtos(new HashSet<>(getTrades().values()));
    }

    public synchronized Stream<TradeDto> getByUnderlying(AssetDto asset) {
        return getTradesStreamCopy()
            .filter(dto -> {
                AssetDto tradedAsset = dto.getAsset();
                if (tradedAsset instanceof OptionDto tradedOption) {
                    return tradedOption.getUnderlying().equals(asset);
                }
                return false;
            });
    }

    public synchronized Stream<TradeDto> getLatentTrades() {
        return getTradesStreamCopy()
            .filter(trade -> trade.getStatus() != CLOSE_EXECUTED)
            .filter(trade -> trade.getStatus() != CANCEL_EXECUTED);
    }

    /**
     * ABC-2
     */
    private void addPreExistingTrades() {
        Set<PositionDto> preExistingPositions = new HashSet<>();
        positionDao.findDtos().stream()
            .filter(position -> position.getAsset().getAssetType() != AssetType.CASH)
            .forEach(
                position -> {
                    if (getTradesStream().noneMatch(trade -> isTradeMatchingPosition(trade, position))) {
                        preExistingPositions.add(position);
                    }
                }
            );
        preExistingPositions.forEach(position -> {
            var trade = new TradeDto(position.getAsset());
            trade.setAmount(position.getAmount());
            trade.setStatus(TradeStatus.OPEN_EXECUTED);
            trade.setUuid(UUID.randomUUID());
            getTrades().put(trade.getUuid(), trade);
        });

    }

    private boolean isTradeMatchingPosition(TradeDto trade, PositionDto position) {
        var isAssetMatch = trade.getAsset().getName().equals(position.getAsset().getName());
        return isAssetMatch && ACTIVE_POS.contains(trade.getStatus());
    }

}
