package com.olexyn.abricore.datastore;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.util.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Set;

import static com.olexyn.abricore.util.Constants.CSV;
import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.SPACE;

public class FileNameUtil {

    static boolean containsAnyToken(Path filePath, Set<String> tokens) {
        boolean result = false;
        for (String token : tokens) {
            boolean tmpResult = containsToken(filePath, token);
            result = result || tmpResult;
        }
        return result;
    }

    static boolean containsToken(Path filePath, String token) {
        String fileName = filePath.getFileName().toString();
        fileName = fileName.replace(CSV, EMPTY);
        String[] words = fileName.split("[_, ]");
        for (String word : words) {
            if (word.toUpperCase().equals(token.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    static Asset mapToFirstAsset(Path path) throws StoreException {
        return AssetService.getNames().stream()
            .filter(str -> containsToken(path, str))
            .map(str -> AssetService.ofName(str.toUpperCase()))
            .findFirst().orElseThrow(StoreException::new);
    }

    static Interval mapToFirstInterval(Path path) throws StoreException {
        return Interval.getFileTokens().stream()
            .filter(str -> containsToken(path, str))
            .map(str -> Interval.ofFileToken(str.toUpperCase()))
            .findFirst().orElseThrow(StoreException::new);
    }

    static Path getProcessedPath(Path path) {
        String fileName = path.getFileName().toString();

        try {
            // see if the filename already contains an Instant
            String[] split = (SPACE + fileName).split(SPACE);
            Instant instant = Instant.parse(split[split.length-1].replace(CSV, EMPTY));
            fileName = fileName.replace(instant.toString(), Instant.now().toString());
        } catch (DateTimeException e) {
            // otherwise add Instant.now
            fileName = path.getFileName().toString().replace(CSV, SPACE + Instant.now().toString() + CSV);
        }
        return Paths.get(Parameters.QUOTES_DIR_PROCESSED + fileName);
    }

}
