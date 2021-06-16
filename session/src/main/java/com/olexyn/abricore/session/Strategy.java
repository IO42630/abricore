package com.olexyn.abricore.session;

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


}