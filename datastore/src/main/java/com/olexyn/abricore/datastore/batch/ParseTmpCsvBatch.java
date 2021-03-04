package com.olexyn.abricore.datastore.batch;

import com.olexyn.abricore.datastore.StoreCsv;
import com.olexyn.abricore.datastore.StoreParameters;
import com.olexyn.abricore.datastore.symbols.Symbols;
import com.olexyn.abricore.model.Interval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class ParseTmpCsvBatch {

    /**
     * Stub ...
     */
    public void parseTmpCsv() throws IOException {
        Path tmpQuotes = Paths.get(StoreParameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(x -> containsAnyToken(x.toString(), Symbols.getNames()))
            .filter(x -> containsAnyToken(x.toString(), Interval.getFileLabels()))
            .map(x -> StoreCsv.getInstance().read(x))
            .forEach(x -> StoreCsv.getInstance().update(x));
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