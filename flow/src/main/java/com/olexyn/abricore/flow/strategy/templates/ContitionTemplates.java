package com.olexyn.abricore.flow.strategy.templates;

import com.olexyn.abricore.model.runtime.strategy.functions.SizingCondition;
import com.olexyn.abricore.model.runtime.strategy.functions.TransactionCondition;
import com.olexyn.abricore.store.functions.condition.And;
import com.olexyn.abricore.store.functions.condition.HasBolTailSize;
import com.olexyn.abricore.store.functions.condition.HasFavorableSide;
import com.olexyn.abricore.store.functions.condition.HasRsiRadius;
import com.olexyn.abricore.store.functions.condition.HasTailDepth;
import com.olexyn.abricore.store.functions.condition.StopLossAtBol;
import com.olexyn.abricore.store.functions.condition.StopLossAtMa;
import com.olexyn.abricore.store.functions.generator.FixedSize;
import com.olexyn.abricore.util.CtxAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import static com.olexyn.abricore.util.num.NumSerialize.fromStr;

@Component
public class ContitionTemplates extends CtxAware {


    protected ContitionTemplates(ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public TransactionCondition fullBuy() {
        return new And(
            new HasFavorableSide(),
            new HasBolTailSize(),
            new HasTailDepth(),
            new HasRsiRadius()
        );
    }

    public TransactionCondition fullSell() {
        return new And(
            new HasBolTailSize(),
            new HasTailDepth(),
            new HasRsiRadius(),
            new StopLossAtMa(),
            new StopLossAtBol()
        );
    }

    public SizingCondition fixed(String value) {
        return new FixedSize(fromStr(value));
    }

}
