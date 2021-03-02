package com.olexyn.abricore.datastore;

import com.olexyn.abricore.datastore.symbols.Symbols;
import com.olexyn.abricore.model.Interval;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class Batch {



    /**
     * Stub ...
     */
    public void parseTmpCsv() throws IOException {
        Path tmpQuotes = Paths.get(StoreParameters.QUOTES_DIR_TMP);
        Files.list(tmpQuotes)
            .filter(x -> containsAnyToken(x.toString(), Symbols.getNames()))
            .filter(x -> containsAnyToken(x.toString(), Interval.getFileLabels()))
            .forEach(x -> StoreCsv.getInstance().read(x));
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