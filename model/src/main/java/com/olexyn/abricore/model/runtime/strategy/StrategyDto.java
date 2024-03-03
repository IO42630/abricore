package com.olexyn.abricore.model.runtime.strategy;


import com.olexyn.abricore.model.runtime.Dto;
import com.olexyn.abricore.model.runtime.TradeDto;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.strategy.functions.DistanceGenerator;
import com.olexyn.abricore.model.runtime.strategy.functions.SizingCondition;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.olexyn.abricore.util.num.NumUtil.toInt;


/**
 * Represents the "StrategyDto" used during a "Trading StrategyDto": <br>
 * - follows one Asset <br>
 * - has a fixed capital amount allocated to it <br>
 * - follows one StrategyDto <br>
 * - is configured and started by the StrategyManager <br>
 * - can be serialized to disk in order to be analyzed
 * - is applied to an Asset through the StrategyDto <br>
 * - the StrategyDto itself is Asset agnostic <br>
 * - is serialized to disk as a member of a StrategyDto for future analysis <br>
 * - can be serialized to disk separately in order to be reused <br>
 * - the sizing conditions determine how the position is entered/exited <br>
 * - - e.g. if sizingIn = x -> x/5 , then when a buyCondition is met, 1/5 of the capital will be exerted. <br>
 * - - afterwards, if another buyCondition is met, the next 1/5 of the capital will be exerted. <br>
 * - - this should allow to gradually enter and exit positions. <br>
 * Note: this class has a natural ordering that is inconsistent with equals.\
 */
@Getter
@Setter
public class StrategyDto implements Serializable, Comparable<StrategyDto>, Dto<StrategyDto> {

    @Serial
    private static final long serialVersionUID = 9007557052219476597L;

    public final UUID uuid = UUID.randomUUID();
    private Long id;
    private String name;
    private AssetDto underlying = null;
    private long allocatedCapital;
    private long minRatio;
    private long maxRatio;
    private Instant from = null;
    private Instant to = null;

    private TransactionCondition callBuyCondition = null;
    private TransactionCondition callSellCondition = null;
    private TransactionCondition putBuyCondition = null;
    private TransactionCondition putSellCondition = null;

    private SizingCondition sizingInCondition = null;
    private SizingCondition sizingOutCondition = null;
    private DistanceGenerator minOptionDistance = null;
    private DistanceGenerator maxOptionDistance = null;
    // how far above the current price should the sell be placed?
    private DistanceGenerator sellDistance = null;
    // how far below the current price should the buy be placed?
    private DistanceGenerator buyDistance = null;

    private VectorDto vector;

    private final List<TradeDto> trades = new ArrayList<>();
    private long fitness = 0L;

    public StrategyDto(String name) {
        this.name = name;
        this.vector = new VectorDto();
    }

    public StrategyDto(String name, VectorDto vector) {
        this.name = name;
        this.vector = vector;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public StrategyDto mergeFrom(StrategyDto other) {
        return this;
    }

    public void setAllocatedCapital(long allocatedCapital) {
        if (allocatedCapital == 0) { return; }
        this.allocatedCapital = allocatedCapital;
    }

    public void setMinRatio(long minRatio) {
        if (0 == (minRatio)) { return; }
        this.minRatio = minRatio;
    }

    public void setMaxRatio(long maxRatio) {
        if (maxRatio == 0) { return; }
        this.maxRatio = maxRatio;
    }

    public void setCallBuyCondition(@Nullable TransactionCondition callBuyCondition) {
        if (callBuyCondition == null) { return; }
        this.callBuyCondition = callBuyCondition;
    }

    public void setCallSellCondition(@Nullable TransactionCondition callSellCondition) {
        if (callSellCondition == null) { return; }
        this.callSellCondition = callSellCondition;
    }

    public void setPutBuyCondition(@Nullable TransactionCondition putBuyCondition) {
        if (putBuyCondition == null) { return; }
        this.putBuyCondition = putBuyCondition;
    }

    public void setPutSellCondition(@Nullable TransactionCondition putSellCondition) {
        if (putSellCondition == null) { return; }
        this.putSellCondition = putSellCondition;
    }

    public void setSizingOutCondition(@Nullable SizingCondition sizingOutCondition) {
        if (sizingOutCondition == null) { return; }
        this.sizingOutCondition = sizingOutCondition;
    }

    /**
     * Minimal distance between price of underlying and strike of option.
     * If the distance is small, the risk the option getting knocked-out increases.
     */
    public DistanceGenerator getMinOptionDistance() {
        return minOptionDistance;
    }

    /**
     * Maximal distance between price of underlying and strike of option.
     * This limits the number of options considered for trading.
     */
    public DistanceGenerator getMaxOptionDistance() {
        return maxOptionDistance;
    }

    public void setFitness(long fitness) {
        if (fitness == 0) { return; }
        this.fitness = fitness;
    }


    @Override
    public String toString() {
        if (fitness == 0) { return "Fitness is NULL."; }
        return Long.toString(toInt(getFitness()));
    }

    /**
     * Greatest FIRST.
     */
    @Override
    public int compareTo(StrategyDto other) {
        if (getFitness() < other.getFitness()) {
            return 1;
        }
        if (getFitness() > other.getFitness()) {
            return -1;
        }
        return 0;
    }

    /**
     * CLONE without fitness and trades.
     */
    public StrategyDto cloneTemplate() {
        StrategyDto clone = new StrategyDto(
            this.getName(),
            this.getVector().clone()
        );
        clone.setUnderlying(this.getUnderlying());
        clone.setAllocatedCapital(this.getAllocatedCapital());
        clone.setMinRatio(this.getMinRatio());
        clone.setMaxRatio(this.getMaxRatio());
        clone.setFrom(this.getFrom());
        clone.setTo(this.getTo());
        clone.setCallBuyCondition(this.getCallBuyCondition());
        clone.setCallSellCondition(this.getCallSellCondition());
        clone.setPutBuyCondition(this.getPutBuyCondition());
        clone.setPutSellCondition(this.getPutSellCondition());
        clone.setSizingInCondition(this.getSizingInCondition());
        clone.setSizingOutCondition(this.getSizingOutCondition());
        clone.setMinOptionDistance(this.getMinOptionDistance());
        clone.setMaxOptionDistance(this.getMaxOptionDistance());
        clone.setSellDistance(this.getSellDistance());
        clone.setBuyDistance(this.getBuyDistance());
        return clone;
    }

    public StrategyDto cloneTemplate(VectorDto vector) {
        var clone = cloneTemplate();
        clone.setVector(vector);
        return clone;
    }

    public Duration getDuration() {
        if (from == null || to == null) {
            return Duration.ofDays(300);
        }
        return Duration.between(from, to);
    }

}
