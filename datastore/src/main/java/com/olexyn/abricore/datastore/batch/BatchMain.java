package com.olexyn.abricore.datastore.batch;

import java.io.IOException;

public class BatchMain {

    public static void main(String... args) throws IOException {
        new ParseTmpCsvBatch().parseTmpCsv();
    }
}
