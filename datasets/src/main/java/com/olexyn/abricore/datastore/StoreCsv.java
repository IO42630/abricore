package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.Stock;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.StockSnapshot;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Map.Entry;
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
    public TreeMap<Instant, AssetSnapshot> read(Asset asset, Interval interval) {
        TreeMap<Instant, AssetSnapshot> out = new TreeMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(StoreParameters.QUOTES_DIR_TMP + "SPCFD_SPX, 30.csv"))) {

            String[] columnOrder;
            String[] lineInArray;


            if ((lineInArray = reader.readNext()) != null) {
                columnOrder = lineInArray;
            } else {
                throw new NullPointerException("");
            }


            while ((lineInArray = reader.readNext()) != null) {
                AssetSnapshot assetSnapshot = new StockSnapshot(new Stock("test"), Interval.M_30);
                for (int i = 0; i < lineInArray.length; i++) {
                    assetSnapshot.assign(columnOrder, i, lineInArray);
                }
                out.put(assetSnapshot.getInstant(), assetSnapshot);
            }
            int br = 0;
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }
        return out;
    }


    public static void main(String... args) {
        TreeMap<Instant, AssetSnapshot> out = StoreCsv.getInstance().read(new Stock("test"), Interval.M_30);
        StoreCsv.getInstance().write(out, Interval.M_30);

    }

    /**
     * Write Map of AssetSnapshot to .csv.
     */
    void write(TreeMap<Instant, AssetSnapshot> assetSnapshotTreeMap, Interval interval) {
        String assetName = assetSnapshotTreeMap.firstEntry().getValue().getAsset().getName();
        String fileName = assetName + "_" + interval.name() + ".csv";
        String path = StoreParameters.QUOTES_DIR_STORE + fileName;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(path)))) {
            String comma = ",";
            String newline = "\n";
            StringBuilder lineBuilder = new StringBuilder();
            int size = 0;
            for (Entry<Instant, AssetSnapshot> entry : assetSnapshotTreeMap.entrySet()) {

                AssetSnapshot assetSnapshot = entry.getValue();

                lineBuilder.append(assetSnapshot.getInstant().toEpochMilli() / 1000).append(comma);
                lineBuilder.append(assetSnapshot.getInterval().name()).append(comma);
                lineBuilder.append(assetSnapshot.getOpen()).append(comma);
                lineBuilder.append(assetSnapshot.getHigh()).append(comma);
                lineBuilder.append(assetSnapshot.getLow()).append(comma);
                lineBuilder.append(assetSnapshot.getClose()).append(comma);
                lineBuilder.append(assetSnapshot.getVolume()).append(newline);
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
    public void update(TreeMap<Instant, AssetSnapshot> newEntries, Interval interval) {

        Asset asset = newEntries.firstEntry().getValue().getAsset();
        TreeMap<Instant, AssetSnapshot> oldMap = StoreCsv.getInstance().read(asset, interval);

        for (Entry<Instant, AssetSnapshot> entry : newEntries.entrySet()) {
            Instant key = entry.getKey();
            if (!oldMap.containsKey(key)) {
                oldMap.put(key, entry.getValue());
            }
        }
        StoreCsv.getInstance().write(oldMap, interval);
    }

    /**
     *
     */
    public void update(Asset asset ){
        // Scan all new downloaded files matching asset, and update.
    }

}
