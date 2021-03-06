package com.olexyn.abricore.util;

public interface Parameters {
    String QUOTES_DIR_TMP = System.getProperty("user.home") + "/docs/abricore/quotes/tmp/";
    String QUOTES_DIR_STORE = System.getProperty("user.home") + "/docs/abricore/quotes/store/";
    String QUOTES_DIR_PROCESSED = System.getProperty("user.home") + "/docs/abricore/quotes/processed/";
    String STRAT_DIR_STORE = System.getProperty("user.home") + "/docs/abricore/strategies/";
    String LOGS_DIR = System.getProperty("user.home") + "/docs/abricore/logs/";
    String SYMBOLS_PATH = System.getProperty("user.dir") + "/datastore/src/main/resources/symbols.json";
    String OPTIONS_PATH = System.getProperty("user.dir") + "/datastore/src/main/resources/options.json";
}
