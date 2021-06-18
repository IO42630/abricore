package com.olexyn.abricore.datastore.batch;

import com.olexyn.abricore.datastore.AssetService;
import com.olexyn.abricore.model.Interval;

import java.io.IOException;

public class BatchMain {

    public static void main(String... args) throws IOException {
        new ParseTmpCsvBatch().parseTmpCsv();
        new MaCalcBatch().calcAllMa(AssetService.ofName("XAGUSD"), Interval.H_1);
    }
}
