package com.olexyn.abricore.flow;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ObserveMode extends WebMode {



    public abstract void updateQuote();

}
