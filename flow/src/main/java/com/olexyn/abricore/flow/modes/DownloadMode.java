package com.olexyn.abricore.flow.modes;

public abstract class DownloadMode extends Mode {

    public abstract void downloadHistoricalData() throws InterruptedException;

}
