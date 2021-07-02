package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.LogUtil;
import com.olexyn.abricore.util.Parameters;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.logging.Logger;

public class TmpCsvService {

    private static final Logger LOGGER = LogUtil.get(TmpCsvService.class);

    /**
     *
     */
    public static void parseTmpCsv() throws IOException {
        LOGGER.info("STARTED parsing TMP CSV.");
        Path tmpQuotes = Paths.get(Parameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(path -> FileNameUtil.containsAnyToken(path, AssetService.getNames()))
            .filter(path -> FileNameUtil.containsAnyToken(path, Interval.getFileTokens()))
            .map(path -> {
                Series series = readFromTmpCsv(path);
                try {
                    Files.move(path, FileNameUtil.getProcessedPath(path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return series;
            })
            .filter(Objects::nonNull)
            .forEach(StoreCsvService::update);
        LOGGER.info("FINISHED parsing TMP CSV.");

    }



    /**
     * Wrapper for readFromDisk(Asset asset, Interval interval).
     */
    private static Series readFromTmpCsv(Path path) {
        Asset asset;
        Interval interval;
        try {
            asset = FileNameUtil.mapToFirstAsset(path);
            interval = FileNameUtil.mapToFirstInterval(path);
            return readFromTmpCsv(path, asset, interval);
        } catch (StoreException e) {
            return null;
        }
    }

    private static Series readFromTmpCsv(Path path, Asset asset, Interval interval) {
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
                throw new StoreException();
            }
            while ((lineInArray = reader.readNext()) != null) {
                AssetSnapshot snapshot = mapDataFromTmpCsvLine(headerArray, lineInArray, asset, interval);
                out.put(snapshot);
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }

    public static AssetSnapshot mapDataFromTmpCsvLine(String[] headerArray, String[] lineArray, Asset asset, Interval interval) {

        AssetSnapshot snapshot = new AssetSnapshot(asset);
        ANum low = null;
        ANum high = null;

        for (int i = 0; i < headerArray.length; i++) {
            String columnName = headerArray[i].toUpperCase().trim();
            if (!TmpHeader.getHeader().contains(columnName)) {
                continue;
            }
            switch (TmpHeader.valueOf(columnName)) {
                case TIME:
                    snapshot.setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
                    break;
                case OPEN:
                    if (interval == Interval.S_1) {
                        snapshot.getPrice().setTraded(ANum.of(lineArray[i]));
                    }
                    break;
                case LOW:
                    if (interval == Interval.S_1) {
                        low = ANum.of(lineArray[i]);
                    }
                    break;
                case HIGH:
                    if (interval == Interval.S_1) {
                        high = ANum.of(lineArray[i]);
                    }
                    break;
                case VOLUME:
                    // Convention: only track volume on the 1 minute interval.
                    if (interval == Interval.S_1) {
                        snapshot.setVolume(ANum.of(lineArray[i]));
                    }
                    break;
                default:
                    break;
            }
        }

        if (high != null && low != null) {
            snapshot.setRange(high.minus(low));
        }

        return snapshot;
    }
}