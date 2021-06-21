package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Header;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import static com.olexyn.abricore.util.Constants.COMMA;
import static com.olexyn.abricore.util.Constants.NEWLINE;
import static com.olexyn.abricore.util.Constants.NULL;



public class StoreCsvService {

    /**
     * Read a Series from Store. <br>
     * This is done by mapping the columns of the CSV to fields recognized by the AssetSnapshot.
     */
    public static Series readFromDisk(Asset asset) {
        Path path = FileNameUtil.getStorePath(asset);
        Series out = new Series(asset);

        try {
            asset = FileNameUtil.mapToFirstAsset(path.getFileName().toString());
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

                System.out.println(lineInArray == null);
                System.out.println(Arrays.deepToString(lineInArray));
                AssetSnapshot assetSnapshot = mapData(headerArray, lineInArray, asset);
                out.put(assetSnapshot.getInstant(), assetSnapshot);
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }



    /**
     * Write AssetSnapshots to Storage in .csv.
     * The columns are manually mapped.
     */
    private static void writeToStore(Series series) {

        Path path = FileNameUtil.getStorePath(series.getAsset());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {

            bufferedWriter.write(Header.getHeader());
            StringBuilder lineBuilder = new StringBuilder();
            int size = 0;
            for (Entry<Instant, AssetSnapshot> entry : series.entrySet()) {

                buildLine(lineBuilder, entry.getValue());
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
     * Update CSV by adding AssesSnapshots from Series .
     */
    public static void update(Series newEntries) {
        Asset asset = newEntries.getAsset();

        Series storedMap = readFromDisk(asset);

        for (Entry<Instant, AssetSnapshot> newEntry : newEntries.entrySet()) {
            storedMap.put(newEntry.getKey(), newEntry.getValue());
        }
        writeToStore(storedMap);
    }

    public static AssetSnapshot mapData(String[] headerArray, String[] lineArray, Asset asset) {
        AssetSnapshot snapshot = new AssetSnapshot(asset);
        for (int i = 0; i < headerArray.length; i++) {
            switch (headerArray[i].toUpperCase().trim()) {
                case "TIME":
                    snapshot.setInstant(Instant.parse(lineArray[i]));
                    break;
                case "PRICE_TRADED":
                    snapshot.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "PRICE_BID":
                    snapshot.getPrice().setBid(ANum.of(lineArray[i]));
                    break;
                case "PRICE_ASK":
                    snapshot.getPrice().setAsk(ANum.of(lineArray[i]));
                    break;
                case "VOLUME":
                    snapshot.setVolume(ANum.of(lineArray[i]));
                    break;
                default:
                    break;
            }
        }
        return snapshot;
    }

    public static void buildLine(StringBuilder lineBuilder, AssetSnapshot snapshot) {
        List<ANum> values = new ArrayList<>();
        values.add(snapshot.getPrice().getTraded());
        values.add(snapshot.getPrice().getBid());
        values.add(snapshot.getPrice().getAsk());
        values.add(snapshot.getVolume());

        lineBuilder.append(snapshot.getInstant().toString()).append(COMMA);
        for (int i = 0 ; i< values.size(); i++) {
            String value = values.get(i) == null ? NULL : values.get(i).toString();
            lineBuilder.append(value);
            if (i + 1 < values.size()) {
                lineBuilder.append(COMMA);
            } else {
                lineBuilder.append(NEWLINE);
            }
        }
    }

}
