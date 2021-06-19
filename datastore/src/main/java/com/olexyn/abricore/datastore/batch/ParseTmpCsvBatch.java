package com.olexyn.abricore.datastore.batch;

import com.olexyn.abricore.datastore.StoreCsvService;
import com.olexyn.abricore.datastore.SymbolsService;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.util.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class ParseTmpCsvBatch {

    /**
     * Stub ...
     */
    public void parseTmpCsv() throws IOException {
        Path tmpQuotes = Paths.get(Parameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(x -> containsAnyToken(x.toString(), SymbolsService.getNames()))
            .filter(x -> containsAnyToken(x.toString(), Interval.getFileLabels()))
            .map(StoreCsvService::readFromDisk)
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
}