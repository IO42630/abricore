package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.SnapShotSeries;
import com.olexyn.abricore.util.Parameters;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

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

import static com.olexyn.abricore.datastore.Symbols.SYMBOLS;

public class StoreCsvService {

    /**
     * Wrapper for readFromDisk(Asset asset, Interval interval).
     */
    public static SnapShotSeries readFromDisk(Path path) {
        Asset asset;
        Interval interval;
        try {
            asset = mapToFirstAsset(path.getFileName().toString(), SYMBOLS);
            interval = mapToFirstInterval(path.getFileName().toString());
            return readFromDisk(asset, interval);
        } catch (StoreException e) {
            return null;
        }
    }

    /**
     * Read a SnapShotSeries from any CSV. <br>
     * This is done by mapping the columns of the CSV to fields recognized by the AssetSnapshot.
     */
    public static SnapShotSeries readFromDisk(Asset asset, Interval interval) {
        Path path = getStorePath(asset, interval);
        SnapShotSeries out = new SnapShotSeries(asset, interval);

        try {
            asset = mapToFirstAsset(path.getFileName().toString(), SYMBOLS);
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
                AssetSnapshot.mapData(assetSnapshot, headerArray, lineInArray);
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
    private static void writeToStore(SnapShotSeries assetSnapshotTreeMap) {

        AssetSnapshot firstSnapshot = assetSnapshotTreeMap.firstEntry().getValue();

        Asset asset = firstSnapshot .getAsset();
        Interval interval = firstSnapshot .getInterval();
        Path path = getStorePath(asset, interval);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {

            bufferedWriter.write(firstSnapshot.getHeader());
            StringBuilder lineBuilder = new StringBuilder();
            int size = 0;
            for (Entry<Instant, AssetSnapshot> entry : assetSnapshotTreeMap.entrySet()) {

                entry.getValue().buildLine(lineBuilder);
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
     * Update CSV by adding AssesSnapshots from SnapShotSeries .
     */
    public static void update(SnapShotSeries newEntries) {
        Asset asset = newEntries.firstEntry().getValue().getAsset();
        Interval interval = newEntries.firstEntry().getValue().getInterval();

        SnapShotSeries storedMap = readFromDisk(asset, interval);

        for (Entry<Instant, AssetSnapshot> newEntry : newEntries.entrySet()) {
            Instant key = newEntry.getKey();
            AssetSnapshot newSnapshot = newEntry.getValue();
            if (storedMap.containsKey(key)) {
                storedMap.get(key).update(newSnapshot);
            } else {
                storedMap.put(key, newSnapshot);
            }
        }
        writeToStore(storedMap);
    }

    private static Path getStorePath(Asset asset, Interval interval) {
        return Paths.get(Parameters.QUOTES_DIR_STORE + asset.getName() + "_" + interval.getFileLabel()+ ".csv");
    }

}
