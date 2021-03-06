package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;
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
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static com.olexyn.abricore.util.Constants.COMMA;
import static com.olexyn.abricore.util.Constants.CSV;
import static com.olexyn.abricore.util.Constants.NEWLINE;
import static com.olexyn.abricore.util.Constants.NULL;



public class StoreCsvService {

    private static final Logger LOGGER = LogUtil.get(StoreCsvService.class);

    /**
     * Read a Series from Store. <br>
     * This is done by mapping the columns of the CSV to fields recognized by the AssetSnapshot.
     */
    public static Series readFromStoreCsv(Asset asset) {
        Path path = getStorePath(asset);
        Series out = new Series(asset);

        try {
            asset = FileNameUtil.mapToFirstAsset(path);
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
                AssetSnapshot assetSnapshot = mapDataFromStoreCsvLine(headerArray, lineInArray, asset);
                out.put(assetSnapshot);
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

        Path path = getStorePath(series.getAsset());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()))) {

            bufferedWriter.write(StoreHeader.getHeader());
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
        LOGGER.info("START updating Series: " + newEntries.getAsset().getName() + ".");

        Asset asset = newEntries.getAsset();

        Series storedMap = readFromStoreCsv(asset);

        for (Entry<Instant, AssetSnapshot> newEntry : newEntries.entrySet()) {
            if (storedMap.containsKey(newEntry.getKey())) {
                // directly updates the existing SnapShot
                storedMap.get(newEntry.getKey()).mergeFrom(newEntry.getValue());
            } else {
                storedMap.put(newEntry.getValue());
            }
        }
        writeToStore(storedMap);
        LOGGER.info("FINISH updating Series: " + newEntries.getAsset().getName() + ".");
    }

    private static AssetSnapshot mapDataFromStoreCsvLine(String[] headerArray, String[] lineArray, Asset asset) {
        AssetSnapshot snapshot = new AssetSnapshot(asset);
        for (int i = 0; i < headerArray.length; i++) {
            switch (StoreHeader.valueOf(headerArray[i].toUpperCase().trim())) {
                case TIME:
                    snapshot.setInstant(Instant.parse(lineArray[i]));
                    break;
                case PRICE_TRADED:
                    snapshot.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case PRICE_BID:
                    snapshot.getPrice().setBid(ANum.of(lineArray[i]));
                    break;
                case PRICE_ASK:
                    snapshot.getPrice().setAsk(ANum.of(lineArray[i]));
                    break;
                case RANGE:
                    snapshot.setRange(ANum.of(lineArray[i]));
                    break;
                case VOLUME:
                    snapshot.setVolume(ANum.of(lineArray[i]));
                    break;
                default:
                    break;
            }
        }
        return snapshot;
    }

    private static void buildLine(StringBuilder lineBuilder, AssetSnapshot snapshot) {
        List<ANum> values = new ArrayList<>();
        values.add(snapshot.getPrice().getTraded());
        values.add(snapshot.getPrice().getBid());
        values.add(snapshot.getPrice().getAsk());
        values.add(snapshot.getRange());
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

    static Path getStorePath(Asset asset) {
        return Paths.get(Parameters.QUOTES_DIR_STORE + asset.getName() + CSV);
    }

}
