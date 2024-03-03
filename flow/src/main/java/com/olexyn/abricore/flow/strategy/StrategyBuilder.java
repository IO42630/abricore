package com.olexyn.abricore.flow.strategy;

import com.olexyn.abricore.flow.strategy.templates.ContitionTemplates;
import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.model.runtime.strategy.functions.DistanceGenerator;
import com.olexyn.abricore.model.runtime.strategy.functions.SizingCondition;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.model.runtime.strategy.vector.VectorDto;
import com.olexyn.abricore.store.functions.condition.HasBolTailSize;
import com.olexyn.abricore.store.functions.condition.IgnoreFalse;
import com.olexyn.abricore.store.functions.condition.IgnoreTrue;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.num.NumSerialize;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;



@Scope("prototype")
@Component
public class StrategyBuilder extends CtxAware {

    private StrategyDto strategy = null;
    private ContitionTemplates cTmpl = null;


    public StrategyBuilder(ConfigurableApplicationContext ctx) {
        super(ctx);
        this.cTmpl = bean(ContitionTemplates.class);
    }

    public StrategyBuilder init(StrategyDto strategy) {
        this.strategy = strategy;
        return this;
    }

    public StrategyBuilder cloneTemplate() {
        return bean(StrategyBuilder.class).init(strategy.cloneTemplate());
    }

    public StrategyBuilder generateRngVector() {
        strategy.setVector(strategy.getVector().generateRng());
        System.out.println("RNG V: " + strategy.getVector());
        return this;
    }

    /**
     * MUTATE with re-calculating fitness.
     */
    public StrategyBuilder mutate(long impulse) {
        strategy.setVector(strategy.getVector().mutate(impulse));
        return this;
    }

    /**
     * COMBINE with re-calculating fitness.
     */
    public StrategyBuilder combine(StrategyDto other) {
        strategy.setVector(strategy.getVector().combine(other.getVector()));
        return this;
    }

    public StrategyBuilder alwaysBuySell() {
        strategy.setCallBuyCondition(bean(IgnoreTrue.class));
        strategy.setPutBuyCondition(bean(IgnoreTrue.class));
        strategy.setCallSellCondition(bean(IgnoreTrue.class));
        strategy.setPutSellCondition(bean(IgnoreTrue.class));
        return this;
    }





    public StrategyBuilder callTailSizeCondition() {
        strategy.setCallBuyCondition(
            bean(HasBolTailSize.class)
        );
        strategy.setCallSellCondition(
            bean(HasBolTailSize.class)
        );
        return this;
    }

    public StrategyBuilder ignorePuts() {
        strategy.setPutBuyCondition(
            bean(IgnoreFalse.class)
        );
        strategy.setPutSellCondition(
            bean(IgnoreFalse.class)
        );
        return this;
    }

    public StrategyDto build() {
        return strategy;
    }

    /* START ======= DUMMY METHODS */

    public StrategyBuilder setId(long id) {
        strategy.setId(id);
        return this;
    }

    public StrategyBuilder setName(String name) {
        strategy.setName(name);
        return this;
    }

    public StrategyBuilder setUnderlying(@Nullable AssetDto underlying) {
        if (underlying != null) {
            strategy.setUnderlying(underlying);
        }
        return this;
    }

    public StrategyBuilder setAllocatedCapital(String allocatedCapital) {
        strategy.setAllocatedCapital(NumSerialize.fromStr(allocatedCapital));
        return this;
    }

    public StrategyBuilder setAllocatedCapital(long allocatedCapital) {
        strategy.setAllocatedCapital(allocatedCapital);
        return this;
    }

    public StrategyBuilder setMinRatio(long minRatio) {
        strategy.setMinRatio(minRatio);
        return this;
    }

    public StrategyBuilder setMaxRatio(long maxRatio) {
        strategy.setMaxRatio(maxRatio);
        return this;
    }

    public StrategyBuilder setCallBuyCondition(TransactionCondition callBuyCondition) {
        strategy.setCallBuyCondition(callBuyCondition);
        return this;
    }

    public StrategyBuilder setCallSellCondition(TransactionCondition callSellCondition) {
        strategy.setCallSellCondition(callSellCondition);
        return this;
    }

    public StrategyBuilder setPutBuyCondition(TransactionCondition putBuyCondition) {
        strategy.setPutBuyCondition(putBuyCondition);
        return this;
    }

    public StrategyBuilder setPutSellCondition(TransactionCondition putSellCondition) {
        strategy.setPutSellCondition(putSellCondition);
        return this;
    }

    public StrategyBuilder setSizingInCondition(SizingCondition sizingInCondition) {
        strategy.setSizingInCondition(sizingInCondition);
        return this;
    }

    public StrategyBuilder setSizingOutCondition(SizingCondition sizingOutCondition) {
        strategy.setSizingOutCondition(sizingOutCondition);
        return this;
    }

    public StrategyBuilder setMinOptionDistance(DistanceGenerator minOptionDistance) {
        strategy.setMinOptionDistance(minOptionDistance);
        return this;
    }

    public StrategyBuilder setMaxOptionDistance(DistanceGenerator maxOptionDistance) {
        strategy.setMaxOptionDistance(maxOptionDistance);
        return this;
    }

    public StrategyBuilder setSellDistance(DistanceGenerator sellDistance) {
        strategy.setSellDistance(sellDistance);
        return this;
    }

    public StrategyBuilder setBuyDistance(DistanceGenerator buyDistance) {
        strategy.setBuyDistance(buyDistance);
        return this;
    }

    public StrategyBuilder setFrom(Instant from) {
        strategy.setFrom(from);
        return this;
    }

    public StrategyBuilder setTo(Instant to) {
        strategy.setTo(to);
        return this;
    }

    public StrategyBuilder setVector(VectorDto vector) {
        strategy.setVector(vector);
        return this;
    }

//    public StrategyBuilder setFitness(long fitness) {
//        strategy.setFitness(fitness);
//        return this;
//    }

}
