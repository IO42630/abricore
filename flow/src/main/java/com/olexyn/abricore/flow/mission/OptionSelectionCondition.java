package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.Option;

import java.io.Serializable;
import java.util.function.Predicate;

@FunctionalInterface
public interface OptionSelectionCondition  extends Predicate<Option>, Serializable {

    @Override
    boolean test(Option series);

}
