package com.olexyn.abricore.model;



public abstract class Asset {

    private final String name;

    private String twSymbol;

    private String sqIsin;

    public Asset(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTwSymbol() {
        return twSymbol;
    }

    public void setTwSymbol(String twSymbol) {
        this.twSymbol = twSymbol;
    }

    public String getSqIsin() {
        return sqIsin;
    }

    public void setSqIsin(String sqIsin) {
        this.sqIsin = sqIsin;
    }



    /**
     *
     * @return if market is currently open. When implementing account for some safety margin - would be bad to execute an order at the instant the market closes.
     * TODO make abstract.
     * TODO better to have MarketEnum which knows its opening times.
     */
    public Boolean isMarketOpen() {
        return true;
    }


}
