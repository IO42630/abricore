package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.olexyn.abricore.datastore.AssetService.SYMBOLS;

public class FileNameUtil {

    public static Asset mapToFirstAsset(String candidate) throws StoreException {
        for (Asset asset : SYMBOLS) {
            if (candidate.contains(asset.getName())) {
                return asset;
            }
        }
        throw new StoreException();
    }

    public static Interval mapToFirstInterval(String candidate) throws StoreException {
        for (Interval interval : Interval.values()) {
            if (candidate.contains(interval.getFileLabel())) {
                return interval;
            }
        }
        throw new StoreException();
    }

    public static Path getStorePath(Asset asset) {
        return Paths.get(Parameters.QUOTES_DIR_STORE + asset.getName() + ".csv");
    }

}
