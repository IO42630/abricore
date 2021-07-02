package com.olexyn.abricore.flow.mission;

import com.olexyn.abricore.datastore.SeriesService;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.options.Option;
import com.olexyn.abricore.model.options.OptionType;
import com.olexyn.abricore.util.ANum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "Strategy" used during a "Trading Mission": <br>
 * - is applied to an Asset through the Mission <br>
 * - the Strategy itself is Asset agnostic <br>
 * - is serialized to disk as a member of a Mission for future analysis <br>
 * - can be serialized to disk separately in order to be reused <br>
 * - the sizing conditions determine how the position is entered/exited <br>
 * - - e.g. if sizingIn = x -> x/5 , then when a buyCondition is met, 1/5 of the capital will be exerted. <br>
 * - - afterwards, if another buyCondition is met, the next 1/5 of the capital will be exerted. <br>
 * - - this should allow to gradually enter and exit positions. <br>
 */
public class Strategy implements Serializable {

    private String name;

    public double minRatio;
    public double maxRatio;

    public Strategy(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<TransactionCondition> buyConditions = new ArrayList<>();
    public List<TransactionCondition> sellConditions = new ArrayList<>();
    public List<TransactionCondition> stopConditions = new ArrayList<>();

    public SizingCondition sizingInCondition = null;
    public SizingCondition sizingOutCondition = null;
    private DistanceGenerator minOptionDistance = null;
    private DistanceGenerator maxOptionDistance = null;


    public boolean isOptionSelectable(Option option) {
        Asset underlying = option.getUnderlying();
        ANum lastUnderlyingPrice = SeriesService.getLastTraded(underlying);
        ANum difference;
        ANum distance = minOptionDistance.generate(underlying);
        if (option.getOptionType() == OptionType.CALL) {
            difference = lastUnderlyingPrice.minus(option.getStrike());
        } else {
            difference = option.getStrike().minus(lastUnderlyingPrice);
        }
        return difference.greater(distance);
    }

    /**
     * Minimal distance between price of underlying and strike of option.
     * If the distance is small, the risk the option getting knocked-out increases.
     */
    public DistanceGenerator getMinOptionDistance() {
        return minOptionDistance;
    }

    public void setMinOptionDistance(DistanceGenerator minOptionDistance) {
        this.minOptionDistance = minOptionDistance;
    }

    /**
     * Maximal distance between price of underlying and strike of option.
     * This limits the number of options considered for trading.
     */
    public DistanceGenerator getMaxOptionDistance() {
        return maxOptionDistance;
    }

    public void setMaxOptionDistance(DistanceGenerator maxOptionDistance) {
        this.maxOptionDistance = maxOptionDistance;
    }
}