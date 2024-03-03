package com.olexyn.abricore.flow.strategy.templates;

import com.olexyn.abricore.flow.strategy.StrategyBuilder;
import com.olexyn.abricore.model.runtime.strategy.StrategyDto;
import com.olexyn.abricore.store.functions.generator.FactorDistance;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.abricore.util.DataUtil;
import com.olexyn.abricore.util.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

import static com.olexyn.abricore.flow.BaseStrategyNames.XAGUSD_DUMMY;
import static com.olexyn.abricore.flow.BaseStrategyNames.XAGUSD_EVOLUTION_TEST;
import static com.olexyn.abricore.flow.BaseStrategyNames.XAGUSD_TRADE_SQ_TEST;
import static com.olexyn.abricore.util.num.Num.EP2;
import static com.olexyn.abricore.util.num.Num.ONE;

@Component
public class StrategyTemplates extends CtxAware {

    private ContitionTemplates cTmpl = null;



    @Autowired
    public StrategyTemplates(ConfigurableApplicationContext ctx) {
        super(ctx);
        this.cTmpl = bean(ContitionTemplates.class);
    }





    public StrategyDto dummy() {
        var vector = bean(VectorTemplates.class).fullVectorDef();
        var strategy = new StrategyDto(XAGUSD_DUMMY, vector);
        return bean(StrategyBuilder.class)
            .init(strategy)
            .setUnderlying(bean(AssetService.class).ofName("XAGUSD"))
            .setMinRatio(ONE)
            .setMaxRatio(ONE)
            .setSizingInCondition(cTmpl.fixed("20"))
            .setSizingOutCondition(cTmpl.fixed("20"))
            .setMinOptionDistance(new FactorDistance(Property.getNum("trade.min.option.distance.factor")))
            .setMaxOptionDistance(new FactorDistance(Property.getNum("trade.max.option.distance.factor")))
            .build();
    }


    public StrategyDto tradeSqTest() {
        return bean(StrategyBuilder.class)
            .init(dummy())
            .setAllocatedCapital(EP2)
            .setFrom(Instant.now().minus(Duration.ofDays(3)))
            .setTo(Instant.now().plus(Duration.ofDays(3)))
            .setSellDistance(new FactorDistance(Property.getNum("trade.sell.distance.factor")))
            .setBuyDistance(new FactorDistance(Property.getNum("trade.buy.distance.factor")))
            .alwaysBuySell()
            .setName(XAGUSD_TRADE_SQ_TEST)
            .build();
    }


    public StrategyDto tradeSq() {
        return bean(StrategyBuilder.class)
            .init(dummy())
            .setFrom(DataUtil.getInstant("trade.start"))
            .setTo(DataUtil.getInstant("trade.end"))
            .build();
    }

    public StrategyDto evolutionTest() {
        return bean(StrategyBuilder.class)
            .init(dummy())
            .setAllocatedCapital(Property.getNum("paper.trade.capital"))
            .setFrom(DataUtil.getInstant("paper.trade.start"))
            .setTo(DataUtil.getInstant("paper.trade.end"))
            .setSizingInCondition(cTmpl.fixed(Property.get("paper.trade.sizing.in.fixed.size")))
            .setSizingOutCondition(cTmpl.fixed(Property.get("paper.trade.sizing.out.fixed.size")))
            .setSellDistance(new FactorDistance(Property.getNum("paper.trade.sell.distance.factor")))
            .setBuyDistance(new FactorDistance(Property.getNum("paper.trade.buy.distance.factor")))
            .setMinOptionDistance(new FactorDistance(Property.getNum("paper.trade.min.option.distance.factor")))
            .setMaxOptionDistance(new FactorDistance(Property.getNum("paper.trade.max.option.distance.factor")))
            .setCallBuyCondition(cTmpl.fullBuy())
            .setCallSellCondition(cTmpl.fullSell())
            .setPutBuyCondition(cTmpl.fullBuy())
            .setPutSellCondition(cTmpl.fullSell())
            .setName(XAGUSD_EVOLUTION_TEST)
            .build();
    }

}
