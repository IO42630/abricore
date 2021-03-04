package com.olexyn.abricore.evaluate;

import com.olexyn.abricore.model.snapshots.AssetSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Strategy {

    // TODO change this from AssetSnapshot to something more useful.
    List<Predicate<AssetSnapshot>> buyConditions = new ArrayList<>();
    List<Predicate<AssetSnapshot>> sellConditions = new ArrayList<>();

    public Strategy(String name) {
        switch(name) {
            case "foo":
                buyConditions.add(new Foo());

        }
    }







}


class Foo implements Predicate<AssetSnapshot> {
    @Override
    public boolean test(AssetSnapshot snapshot) {
        return false;
    }
}