package com.olexyn.abricore.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

public interface Constants {
    String EMPTY = "";
    String SPACE = " ";
    String COMMA = ",";
    String COL = ":";
    String SEMICOLON = ";";
    String DOT = ".";
    String APOSTROPHE = "'";
    String L_BRACE = "(";
    String R_BRACE = ")";
    String DOT_REGEX = "\\.";
    String NEWLINE = "\n";
    String NULL_STR = "null";
    String ZERO_STR = "0";
    String UL = "_";
    String EQ = "=";
    String SLASH = "/";
    String DASH = "-";
    char DASH_C = '-';
    String CSV = ".csv";
    String JSON = ".json";
    Long SECONDS = 1000L;
    Long MINUTES = 60L;
    Long HOURS = 60L;
    String HREF = "href";
    String HOME = "data.home";
    String WORKING_DIR = "user.dir";
    String BUTTON = "button";
    String INPUT = "input";
    String TABLE = "table";
    String DIV = "div";
    String A = "a";
    String SPAN = "span";
    String ID = "id";
    String VALUE = "value";
    Charset CHARSET = StandardCharsets.UTF_8;
    Random RNG = new Random();
    String NULL_INPUT_MESSAGE = "expected parameter was NULL";
    String CHF = "CHF";
    // DURATIONS
    Duration MS1 = Duration.ofMillis(1);
    Duration MS10 = Duration.ofMillis(10);
    Duration MS100 = Duration.ofMillis(100);
    Duration S0 = Duration.ofSeconds(0);
    Duration S1 = Duration.ofSeconds(1);
    Duration S2 = Duration.ofSeconds(2);
    Duration S3 = Duration.ofSeconds(3);
    Duration S4 = Duration.ofSeconds(4);
    Duration S5 = Duration.ofSeconds(5);
    Duration S10 = Duration.ofSeconds(10);
    Duration S15 = Duration.ofSeconds(15);
    Duration S30 = Duration.ofSeconds(30);
    Duration M1 = Duration.ofMinutes(1);
    Duration M2 = Duration.ofMinutes(2);
    Duration M3 = Duration.ofMinutes(3);
}
