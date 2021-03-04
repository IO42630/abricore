package com.olexyn.abricore.datastore;

import com.olexyn.abricore.calc.Calc;
import com.olexyn.abricore.datastore.symbols.Symbols;
import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import static com.olexyn.abricore.common.Constants.*;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import static com.olexyn.abricore.model.snapshots.IndicatorRange.*;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    public TreeMap<Instant, AssetSnapshot> readFromStore(Asset asset, Interval interval) {
        return read(getStorePath(asset, interval));
    }

    /**
     * Read Map of AssetSnapshot from .csv.
     */
    public TreeMap<Instant, AssetSnapshot> read(Path path) {
        TreeMap<Instant, AssetSnapshot> out = new TreeMap<>();
        Asset asset;
        Interval interval;

        try {
            asset = mapToFirstAsset(path.getFileName().toString(), Symbols.getList());
            interval = mapToFirstInterval(path.getFileName().toString());
        } catch (StoreException e) {
            return  out;
        }

        try (CSVReader reader = new CSVReader(new FileReader(path.toFile()))) {
            String[] headerArray;
            String[] lineInArray;

            headerArray = reader.readNext();
            if (headerArray == null) {
                throw new NullPointerException("");
            }

            while ((lineInArray = reader.readNext()) != null) {
                AssetSnapshot assetSnapshot = new AssetSnapshot(asset, interval);
                AssetSnapshot.loadData(assetSnapshot, headerArray, lineInArray);
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
     * Write AssetSnapshots to Storage in .csv.
     * The columns are manually mapped.
     */
    void write(TreeMap<Instant, AssetSnapshot> assetSnapshotTreeMap) {

        Asset asset = assetSnapshotTreeMap.firstEntry().getValue().getAsset();
        Interval interval = assetSnapshotTreeMap.firstEntry().getValue().getInterval();
        Path path = getStorePath(asset, interval);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {

            bufferedWriter.write("time,open,high,low,close,volume\n");
            StringBuilder lineBuilder = new StringBuilder();
            int size = 0;
            for (Entry<Instant, AssetSnapshot> entry : assetSnapshotTreeMap.entrySet()) {

                AssetSnapshot assetSnapshot = entry.getValue();

                buildLine(
                    lineBuilder,
                    assetSnapshot.getInstant(),
                    assetSnapshot.getOpen(),
                    assetSnapshot.getHigh(),
                    assetSnapshot.getLow(),
                    assetSnapshot.getClose(),
                    assetSnapshot.getVolume(),
                    assetSnapshot.getMa().get(R5),
                    assetSnapshot.getMa().get(R10),
                    assetSnapshot.getMa().get(R20),
                    assetSnapshot.getMa().get(R50),
                    assetSnapshot.getMa().get(R100),
                    assetSnapshot.getMa().get(R200)
                );
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

    private void buildLine(StringBuilder lineBuilder, Instant instant, Long... values) {
        lineBuilder.append(instant.toEpochMilli() / 1000).append(COMMA);
        for (int i = 0 ; i< values.length; i++) {
            lineBuilder.append(Calc.parseString(values[i]));
                if (i + 1 < values.length) {
                    lineBuilder.append(COMMA);
                } else {
                    lineBuilder.append(NEWLINE);
                }
        }
    }

    /**
     * Update .csv by adding new entries from Map of AssesSnapshot.
     */
    public void update(TreeMap<Instant, AssetSnapshot> newEntries) {
        Asset asset = newEntries.firstEntry().getValue().getAsset();
        Interval interval = newEntries.firstEntry().getValue().getInterval();


        TreeMap<Instant, AssetSnapshot> storedMap = read(getStorePath(asset, interval));


        for (Entry<Instant, AssetSnapshot> entry : newEntries.entrySet()) {
            Instant key = entry.getKey();
            if (!storedMap.containsKey(key)) {
                storedMap.put(key, entry.getValue());
            }
        }
        StoreCsv.getInstance().write(storedMap);
    }

    private Path getStorePath(Asset asset, Interval interval) {
        return Paths.get(StoreParameters.QUOTES_DIR_STORE + asset.getName() + "_" + interval.getFileLabel()+ ".csv");
    }

}
