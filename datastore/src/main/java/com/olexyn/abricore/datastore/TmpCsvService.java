package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.model.snapshots.Series;
import com.olexyn.abricore.util.ANum;
import com.olexyn.abricore.util.Parameters;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static com.olexyn.abricore.util.Constants.CSV;
import static com.olexyn.abricore.util.Constants.EMPTY;

public class TmpCsvService {

    /**
     * TODO fix.
     */
    public static void parseTmpCsv() throws IOException {
        Path tmpQuotes = Paths.get(Parameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(x -> containsAnyToken(x, AssetService.getNames()))
            .filter(x -> containsAnyToken(x, Interval.getFileLabels()))
            .map(TmpCsvService::readFromDisk)
            .filter(Objects::nonNull)
            .forEach(StoreCsvService::update);
    }

    private static boolean containsAnyToken(Path candidate, Set<String> tokens) {
        String fileName = candidate.getFileName().toString();
        fileName = fileName.replace(CSV, EMPTY);
        String[] words = fileName.split("[_, ]");
        for (String word : words) {
            for (String token : new ArrayList<>(tokens)) {
                if (word.equals(token)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Wrapper for readFromDisk(Asset asset, Interval interval).
     */
    private static Series readFromDisk(Path path) {
        Asset asset;
        Interval interval;
        try {
            asset = FileNameUtil.mapToFirstAsset(path.getFileName().toString());
            interval = FileNameUtil.mapToFirstInterval(path.getFileName().toString());
            return readFromDisk(path, asset, interval);
        } catch (StoreException e) {
            return null;
        }
    }

    private static Series readFromDisk(Path path, Asset asset, Interval interval) {
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
                throw new StoreException();
            }
            while ((lineInArray = reader.readNext()) != null) {
                AssetSnapshot snapshot = mapData(headerArray, lineInArray, asset, interval);
                out.put(snapshot.getInstant(), snapshot);
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }

    public static AssetSnapshot mapData(String[] headerArray, String[] lineArray, Asset asset, Interval interval) {

        AssetSnapshot snapshot = new AssetSnapshot(asset);
        ANum low = null;
        ANum high = null;

        for (int i = 0; i < headerArray.length; i++) {
            String columnName = headerArray[i].toUpperCase().trim();
            if (Arrays.stream(TmpHeader.values()).anyMatch(x -> !x.name().equals(columnName))) {
                break;
            }
            switch (TmpHeader.valueOf(columnName)) {
                case TIME:
                    snapshot.setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
                    break;
                case OPEN:
                    snapshot.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case LOW:
                    low = ANum.of(lineArray[i]);
                    break;
                case HIGH:
                    high = ANum.of(lineArray[i]);
                    break;
                case VOLUME:
                    // Convention: only track volume on the 1 minute interval.
                    if (interval == Interval.M_1) {
                        snapshot.setVolume(ANum.of(lineArray[i]));
                    }
                    break;
                default:
                    break;
            }
        }

        if (interval == Interval.M_1 && high != null && low != null) {
            snapshot.setRange(high.minus(low));
        }

        return snapshot;
    }
}