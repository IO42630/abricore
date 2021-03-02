package com.olexyn.abricore.datastore;

import com.olexyn.abricore.datastore.symbols.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import static com.olexyn.abricore.common.Constants.*;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.StockSnapshot;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class StoreCsv {

    private static StoreCsv instance = null;

    private StoreCsv() {}

    public static synchronized StoreCsv getInstance() {
        if (instance == null) {
            instance = new StoreCsv();
        }
        return instance;
    }

    /**
     * Read Map of AssetSnapshot from .csv.
     */
    public TreeMap<Instant, AssetSnapshot> read(Path path) {
        TreeMap<Instant, AssetSnapshot> out = new TreeMap<>();
        Asset protoAsset;
        Interval protoInterval;

        try {
            protoAsset = mapToFirstAsset(path.getFileName().toString(), Symbols.getList());
            protoInterval = mapToFirstInterval(path.getFileName().toString());
        } catch (StoreException e) {
            return  out;
        }

        try (CSVReader reader = new CSVReader(new FileReader(path.toFile()))) {

            String[] columnOrder;
            String[] lineInArray;

            if ((lineInArray = reader.readNext()) != null) {
                columnOrder = lineInArray;
            } else {
                throw new NullPointerException("");
            }

            while ((lineInArray = reader.readNext()) != null) {
                AssetSnapshot assetSnapshot = new StockSnapshot(protoAsset, protoInterval);
                for (int i = 0; i < lineInArray.length; i++) {
                    assetSnapshot.assign(columnOrder, i, lineInArray);
                }
                out.put(assetSnapshot.getInstant(), assetSnapshot);
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }

    private static Asset mapToFirstAsset(String candidate, Set<Asset> tokens) throws StoreException {
        for (Asset asset : new ArrayList<>(tokens)) {
            if (candidate.contains(asset.getName())) {
                return asset;
            }
        }
        throw new StoreException();
    }

    private static Interval mapToFirstInterval(String candidate) throws StoreException {
        for (Interval interval : Interval.values()) {
            if (candidate.contains(interval.getFileLabel())) {
                return interval;
            }
        }
        throw new StoreException();
    }

    /**
     * Write Map of AssetSnapshot to .csv.
     */
    void write(TreeMap<Instant, AssetSnapshot> assetSnapshotTreeMap) {

        Asset asset = assetSnapshotTreeMap.firstEntry().getValue().getAsset();
        Interval interval = assetSnapshotTreeMap.firstEntry().getValue().getInterval();
        Path path = oldAssetPath(asset, interval);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {

            bufferedWriter.write("time,open,high,low,close,volume\n");
            StringBuilder lineBuilder = new StringBuilder();
            int size = 0;
            for (Entry<Instant, AssetSnapshot> entry : assetSnapshotTreeMap.entrySet()) {

                AssetSnapshot assetSnapshot = entry.getValue();

                lineBuilder.append(assetSnapshot.getInstant().toEpochMilli() / 1000).append(COMMA);
                lineBuilder.append(assetSnapshot.getOpen()).append(COMMA);
                lineBuilder.append(assetSnapshot.getHigh()).append(COMMA);
                lineBuilder.append(assetSnapshot.getLow()).append(COMMA);
                lineBuilder.append(assetSnapshot.getClose()).append(COMMA);
                lineBuilder.append(assetSnapshot.getVolume()).append(NEWLINE);
                size++;

                if (size > 100) {
                    bufferedWriter.write(lineBuilder.toString());
                    lineBuilder = new StringBuilder();
                    size = 0;
                }
            }
            bufferedWriter.write(lineBuilder.toString());
        } catch (IOException e) {
            //
        }
    }

    /**
     * Update .csv by adding new entries from Map of AssesSnapshot.
     */
    public void update(TreeMap<Instant, AssetSnapshot> newEntries) {
        Asset asset = newEntries.firstEntry().getValue().getAsset();
        Interval interval = newEntries.firstEntry().getValue().getInterval();


        TreeMap<Instant, AssetSnapshot> oldMap = read(oldAssetPath(asset, interval));


        for (Entry<Instant, AssetSnapshot> entry : newEntries.entrySet()) {
            Instant key = entry.getKey();
            if (!oldMap.containsKey(key)) {
                oldMap.put(key, entry.getValue());
            }
        }
        StoreCsv.getInstance().write(oldMap);
    }

    private Path oldAssetPath(Asset asset, Interval interval) {
        return Paths.get(StoreParameters.QUOTES_DIR_STORE + asset.getName() + "_" + interval.getFileLabel()+ ".csv");
    }

}
