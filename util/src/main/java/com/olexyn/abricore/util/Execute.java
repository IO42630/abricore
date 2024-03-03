package com.olexyn.abricore.util;

import com.olexyn.abricore.util.log.LogU;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Execute {


    /**
     * @param cmd an array representing a shell command
     * @return <i>TwoBr</i>  class, containing two BufferedReaders,
     * <i>output</i> and  <i>error</i>
     * @see <i>output</i>  BufferedReader, corresponds to STDOUT
     * <i>error</i>  BufferedReader, corresponds to STDERR
     */
    public TwoBr execute(String[] cmd) {
        TwoBr twobr = new TwoBr();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            twobr.output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            twobr.error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            LogU.warnPlain(e.getMessage(), e);
        }
        return twobr;
    }

    public static class TwoBr {
        public BufferedReader output;
        public BufferedReader error;
    }
}
