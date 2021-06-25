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
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TmpCsvService {

    /**
     * TODO fix.
     */
    public void parseTmpCsv() throws IOException {
        Path tmpQuotes = Paths.get(Parameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(x -> containsAnyToken(x.toString(), AssetService.getNames()))
            .filter(x -> containsAnyToken(x.toString(), Interval.getFileLabels()))
            .map(TmpCsvService::readFromDisk)
            .filter(Objects::nonNull)
            .forEach(StoreCsvService::update);
    }

    private static boolean containsAnyToken(String candidate, Set<String> tokens) {
        for (String token : new ArrayList<>(tokens)) {
            if (candidate.contains(token)) {
                return true;
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
                throw new NullPointerException("");
            }
            while ((lineInArray = reader.readNext()) != null) {
                System.out.println(lineInArray == null);
                System.out.println(Arrays.deepToString(lineInArray));
                mapData(headerArray, lineInArray, asset, interval).forEach(x -> out.put(x.getInstant(), x));
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }

    private static List<AssetSnapshot> mapData(String[] headerArray, String[] lineArray, Asset asset, Interval interval) {

        AssetSnapshot open = new AssetSnapshot(asset);
        AssetSnapshot high = new AssetSnapshot(asset);
        AssetSnapshot low = new AssetSnapshot(asset);
        AssetSnapshot close = new AssetSnapshot(asset);

        for (int i = 0; i < headerArray.length; i++) {
            switch (headerArray[i].toUpperCase().trim()) {
                case "TIME":
                    open.setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
                    break;
                case "OPEN":
                    open.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "HIGH":
                    high.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "LOW":
                    low.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "CLOSE":
                    close.getPrice().setTraded(ANum.of(lineArray[i]));
                    break;
                case "VOLUME":
                    ANum avgVolume = ANum.of(lineArray[i]).div(new ANum(3)).num();
                    high.setVolume(avgVolume);
                    low.setVolume(avgVolume);
                    close.setVolume(avgVolume);
                    break;
                default:
                    break;
            }
        }

        Duration oneThird = interval.duration.dividedBy(3);
        Duration twoThirds = interval.duration.dividedBy(3).multipliedBy(2);
        Duration threeThirds = interval.duration;

        close.setInstant(open.getInstant().plus(threeThirds));
        if (open.getPrice().getTraded().lesser(close.getPrice().getTraded())) {
            low.setInstant(open.getInstant().plus(oneThird));
            high.setInstant(open.getInstant().plus(twoThirds));
        } else {
            high.setInstant(open.getInstant().plus(oneThird));
            low.setInstant(open.getInstant().plus(twoThirds));
        }

        return List.of(high, low, close);
    }
}