package com.olexyn.abricore.store.csv;

import com.olexyn.abricore.model.runtime.assets.AssetDto;
import com.olexyn.abricore.model.runtime.snapshots.Series;
import com.olexyn.abricore.model.runtime.snapshots.SnapshotDto;
import com.olexyn.abricore.store.dao.SnapshotDao;
import com.olexyn.abricore.store.runtime.AssetService;
import com.olexyn.abricore.util.Property;
import com.olexyn.abricore.util.enums.Interval;
import com.olexyn.abricore.util.exception.StoreException;
import com.olexyn.abricore.util.log.LogU;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;

import static com.olexyn.abricore.util.Constants.CHARSET;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Component
public class TmpCsvStore {

    SnapshotDao snapshotDao;
    AssetService assetService;
    FileNameUtil fileNameUtil;

    @Autowired
    public TmpCsvStore(
        SnapshotDao snapshotDao,
        AssetService assetService,
        FileNameUtil fileNameUtil
    ) {
        this.assetService = assetService;
        this.snapshotDao = snapshotDao;
        this.fileNameUtil = fileNameUtil;
    }

    public void readTmpCsvToDb() throws IOException {
        LogU.infoStart("parsing TMP CSV.");
        Path tmpQuoteDir = Paths.get(Property.get("quotes.dir.tmp"));

        try (var pathStream = Files.list(tmpQuoteDir)) {
            pathStream
                .filter(path -> fileNameUtil.containsAnyToken(path, assetService.getNames()))
                .filter(path -> fileNameUtil.containsAnyToken(path, Interval.getFileTokens()))
                .map(path -> new SimpleEntry<>(path, readFromTmpCsv(path)))
                .peek(entry -> moveToProcessed(entry.getKey(), entry.getValue().getAsset()))
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(SimpleEntry::getValue)
                .forEach(series -> snapshotDao.save(series));

        }
        LogU.infoEnd("parsing TMP CSV.");
    }

    private void moveToProcessed(Path tmpPath, AssetDto asset) {
        try {
            Files.move(tmpPath, fileNameUtil.getProcessedPath(tmpPath, asset), REPLACE_EXISTING);
        } catch (IOException e) {
            LogU.infoPlain(e.getMessage());
        }
    }

    /**
     * Wrapper for readFromDisk(Asset asset, Interval interval).
     */
    private @Nullable Series readFromTmpCsv(Path path) {
        AssetDto asset;
        Interval interval;
        try {
            asset = fileNameUtil.mapToFirstAsset(path);
            interval = fileNameUtil.mapToFirstInterval(path);
            return readFromTmpCsv(path, asset, interval);
        } catch (StoreException e) {
            LogU.warnPlain(e.getMessage());
            return null;
        }
    }

    private Series readFromTmpCsv(Path path, AssetDto asset, Interval interval) {
        Series out = new Series(asset);

        try {
            asset = fileNameUtil.mapToFirstAsset(path);
        } catch (StoreException e) {
            return out;
        }

        try (CSVReader reader = new CSVReader(new FileReader(path.toFile(), CHARSET))) {
            String[] headerArray;
            String[] lineInArray;

            headerArray = reader.readNext();
            if (headerArray == null) {
                throw new StoreException();
            }
            while ((lineInArray = reader.readNext()) != null) {
                SnapshotDto snapshot = mapDataFromTmpCsvLine(headerArray, lineInArray, asset, interval);
                out.put(snapshot);
            }
        } catch (CsvValidationException | IOException e) {
            out.clear();
        }
        return out;
    }

    private SnapshotDto mapDataFromTmpCsvLine(String[] headerArray, String[] lineArray, AssetDto asset, Interval interval) {

        var snapshot = new SnapshotDto(asset);
        long low = 0;
        long high = 0;

        for (int i = 0; i < headerArray.length; i++) {
            String columnName = headerArray[i].toUpperCase().trim();
            if (!TmpCsvHeader.getHeader().contains(columnName)) {
                continue;
            }
            switch (TmpCsvHeader.valueOf(columnName)) {
                case TIME -> snapshot.setInstant(Instant.ofEpochSecond(Long.parseLong(lineArray[i])));
                case OPEN -> {
                    if (interval == Interval.S_1) {
                        snapshot.setTradePrice(fromStr(lineArray[i]));
                    }
                }
                case LOW -> {
                    if (interval == Interval.S_1) {
                        low = fromStr(lineArray[i]);
                    }
                }
                case HIGH -> {
                    if (interval == Interval.S_1) {
                        high = fromStr(lineArray[i]);
                    }
                }
                case VOLUME -> {
                    // Convention: only track volume on the 1 minute interval.
                    if (interval == Interval.S_1) {
                        snapshot.setVolume(fromStr(lineArray[i]));
                    }
                }
                default -> {
                }
            }
        }
        if (high != 0 && low != 0) {
            snapshot.setRange(high - low);
        }

        return snapshot;
    }
}
