package com.olexyn.abricore.store;

import com.olexyn.abricore.model.data.SnapshotDistanceEntity;
import com.olexyn.abricore.model.data.PositionEntity;
import com.olexyn.abricore.model.data.SnapshotEntity;
import com.olexyn.abricore.model.data.SymbolEntity;
import com.olexyn.abricore.model.data.TradeEntity;
import com.olexyn.abricore.model.data.VectorEntity;
import com.olexyn.abricore.model.runtime.PositionDto;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.assets.DummyAssetDto;
import com.olexyn.abricore.model.runtime.assets.OptionDto;
import com.olexyn.abricore.model.runtime.assets.UnderlyingAssetDto;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDistanceDto;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.model.runtime.strategy.vector.BoundParam;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorKey;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.store.runtime.SeriesService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.exception.MissingException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.olexyn.abricore.util.num.NumSerialize.fromStr;
import static com.olexyn.abricore.util.num.NumSerialize.toStr;

@Component
public class Mapper extends CtxAware {


    protected Mapper(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public TradeDto toTradeDto(TradeEntity entity) {
        TradeDto dto = new TradeDto(new DummyAssetDto(entity.getAsset()));
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setBuyInstant(entity.getBuyInstant());
        dto.setSellInstant(entity.getSellInstant());
        dto.setAmount(fromStr(entity.getAmount()));
        dto.setBuyPrice(fromStr(entity.getBuyPrice()));
        dto.setSellPrice(fromStr(entity.getSellPrice()));
        dto.setBuyId(entity.getBuyId());
        dto.setSellId(entity.getSellId());
        dto.setBuyFee(fromStr(entity.getBuyFee()));
        dto.setSellFee(fromStr(entity.getSellFee()));
        dto.setUuid(entity.getUuid());
        return dto;
    }

    public TradeEntity toTradeEntity(TradeDto dto) {
        if (dto == null) {
            throw new MissingException();
        }
        TradeEntity entity = new TradeEntity();
        entity.setId(dto.getId());
        entity.setAsset(dto.getAsset().getName());
        entity.setStatus(dto.getStatus());
        entity.setBuyInstant(dto.getBuyInstant());
        entity.setSellInstant(dto.getSellInstant());
        entity.setAmount(toStr(dto.getAmount()));
        entity.setBuyPrice(toStr(dto.getBuyPrice()));
        entity.setSellPrice(toStr(dto.getSellPrice()));
        entity.setBuyId(dto.getBuyId());
        entity.setSellId(dto.getSellId());
        entity.setBuyFee(toStr(dto.getBuyFee()));
        entity.setSellFee(toStr(dto.getSellFee()));
        entity.setUuid(dto.getUuid());
        return entity;
    }

    public SnapshotDto toSnapShotDto(SnapshotEntity entity) {
        SnapshotDto dto = new SnapshotDto();
        dto.setAsset(new DummyAssetDto(entity.getAsset()));
        dto.setId(entity.getId());
        dto.setInstant(entity.getInstant());
        dto.setTradePrice(fromStr(entity.getTradedPrice()));
        dto.setAskPrice(fromStr(entity.getAskPrice()));
        dto.setBidPrice(fromStr(entity.getBidPrice()));
        dto.setRange(fromStr(entity.getRange()));
        dto.setVolume(fromStr(entity.getVolume()));
        dto.setTouched(false);
        return dto;
    }

    public SnapshotEntity toSnapShotEntity(SnapshotDto dto) {
        SnapshotEntity entity = new SnapshotEntity();
        if (dto == null || dto.getAsset() == null || dto.getInstant() == null) {
            throw new MissingException();
        }
        entity.setId(dto.getId());
        entity.setAsset(dto.getAsset().getName());
        entity.setInstant(dto.getInstant());
        entity.setTradedPrice(toStr(dto.getTradePrice()));
        entity.setAskPrice(toStr(dto.getAskPrice()));
        entity.setBidPrice(toStr(dto.getBidPrice()));
        entity.setRange(toStr(dto.getRange()));
        entity.setVolume(toStr(dto.getVolume()));
        return entity;
    }

    public PositionDto toPositionDto(PositionEntity entity) {
        PositionDto dto = new PositionDto();
        dto.setId(entity.getId());
        dto.setAsset(new DummyAssetDto(entity.getAsset()));
        dto.setStatus(entity.getStatus());
        dto.setAmount(fromStr(entity.getAmount()));
        dto.setPrice(fromStr(entity.getPrice()));
        return dto;
    }

    public PositionEntity toPositionEntity(PositionDto dto) {
        PositionEntity entity = new PositionEntity();
        if (dto == null || dto.getAsset() == null) {
            throw new MissingException();
        }
        entity.setId(dto.getId());
        entity.setAsset(dto.getAsset().getName());
        entity.setStatus(dto.getStatus());
        entity.setAmount(toStr(dto.getAmount()));
        entity.setPrice(toStr(dto.getPrice()));
        return entity;
    }

    public AssetDto toAssetDto(SymbolEntity entity) {
        AssetDto dto;
        if (entity.getUnderlying() == null) {
            dto = new UnderlyingAssetDto(entity.getName());
        } else {
            dto = new OptionDto(entity.getName());
        }
        dto.setId(entity.getId());
        dto.setTwSymbol(entity.getTwSymbol());
        dto.setAssetType(entity.getAssetType());
        dto.setSqIsin(entity.getSqIsin());
        dto.setCurrency(entity.getCurrency());
        dto.setExchange(entity.getExchange());
        if (dto instanceof OptionDto optionDto) {
            optionDto.setStrike(fromStr(entity.getStrike()));
            optionDto.setOptionType(entity.getOptionType());
            optionDto.setExpiry(entity.getExpiry());
            optionDto.setRatio(fromStr(entity.getRatio()));
            optionDto.setUnderlying(new UnderlyingAssetDto(entity.getUnderlying()));
            optionDto.setStatus(entity.getStatus());
        }
        return dto;
    }


    public SymbolEntity toSymbolEntity(AssetDto dto) {
        var entity = new SymbolEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setTwSymbol(dto.getTwSymbol());
        entity.setAssetType(dto.getAssetType());
        entity.setSqIsin(dto.getSqIsin());
        entity.setCurrency(dto.getCurrency());
        entity.setExchange(dto.getExchange());
        entity.setStrike(toStr(0));
        entity.setRatio(toStr(0));
        if (dto instanceof OptionDto optionDto) {
            entity.setStrike(toStr(optionDto.getStrike()));
            entity.setOptionType(optionDto.getOptionType());
            entity.setExpiry(optionDto.getExpiry());
            entity.setRatio(toStr(optionDto.getRatio()));
            if (optionDto.getUnderlying() != null) {
                entity.setUnderlying(optionDto.getUnderlying().getName());
            }
            entity.setStatus(optionDto.getStatus());
        }
        return entity;
    }

    public VectorEntity toVectorEntity(VectorDto dto) {
        var entity = new VectorEntity();
        entity.setId(dto.getId());
        entity.setProfitByDay(dto.getProfitByDay());
        entity.setProfitByVolume(dto.getProfitByVolume());
        entity.setRating(dto.getRating());
        entity.setSampleCount(dto.getSampleCount());
        entity.setAvgDuration(dto.getAvgDuration());

        var paramSb = new StringBuilder();
        dto.getParamMap().forEach((key, value) -> paramSb
            .append(key)
            .append(',')
            .append(toStr(value.getValue()))
            .append(',')
            .append(toStr(value.getLowerBound()))
            .append(',')
            .append(toStr(value.getUpperBound()))
            .append(',')
            .append(toStr(value.getPrecision()))
            .append(';')
        );
        entity.setParamMap(MapperHelper.toPrettyClob(paramSb.toString()));
        return entity;
    }

    public VectorDto toVectorDto(VectorEntity entity) {
        var dto = new VectorDto();
        dto.setId(entity.getId());
        dto.setProfitByDay(entity.getProfitByDay());
        dto.setProfitByVolume(entity.getProfitByVolume());
        dto.setRating(entity.getRating());
        dto.setSampleCount(entity.getSampleCount());
        dto.setAvgDuration(entity.getAvgDuration());
        var paramStr = MapperHelper.fromPrettyClob(entity.getParamMap());
        Arrays.stream(paramStr.split(";")).forEach(param -> {
            var paramArr = param.split(",");
            var key = VectorKey.valueOf(paramArr[0]);
            var value = fromStr(paramArr[1]);
            var lowerBound = fromStr(paramArr[2]);
            var upperBound = fromStr(paramArr[3]);
            var precision = fromStr(paramArr[4]);
            var boundParam = new BoundParam(lowerBound, upperBound, precision);
            boundParam.setValue(value);
            dto.getParamMap().put(key, boundParam);
        });
        return dto;
    }

    public SnapshotDistanceEntity toSnapshotDistanceEntity(SnapshotDistanceDto dto) {
        var entity = new SnapshotDistanceEntity();
        entity.setId(dto.getId());
        entity.setAsset(dto.getAsset().getName());
        entity.setSnapshotDistanceType(dto.getSnapshotDistanceType());
        entity.setStart(dto.getStart());
        entity.setEnd(dto.getEnd());
        entity.setLength(dto.getLength());
        return entity;
    }

    public SnapshotDistanceDto toSnapshotDistanceDto(SnapshotDistanceEntity entity) {
        var asset = bean(AssetService.class).ofName(entity.getAsset());
        var series = bean(SeriesService.class).of(asset);
        assert series != null;
        var dto = new SnapshotDistanceDto(series, entity.getStart());
        dto.setId(entity.getId());
        dto.setSnapshotDistanceType(entity.getSnapshotDistanceType());
        dto.setEnd(entity.getEnd());
        dto.setLength(entity.getLength());
        return dto;
    }

}
