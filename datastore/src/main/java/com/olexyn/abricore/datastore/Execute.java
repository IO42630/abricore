package com.olexyn.abricore.datastore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Execute {


    /**
     * @param cmd an array representing a shell command
     * @return <i>TwoBr</i>  class, containing two BufferedReaders,
     * <i>output</i> and  <i>error</i>
     * @see <i>output</i>  BufferedReader, corresponds to STDOUT
     * <i>error</i>  BufferedReader, corresponds to STDERR
     */
    public TwoBr execute(String cmd[]) {
        TwoBr twobr = new TwoBr();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            twobr.output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            twobr.error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return twobr;
    }


    public TwoBr execute(List<String> cmd) {

        String[] cmdArr = new String[cmd.size()];
        for (int i = 0; i < cmd.size(); i++) {
            cmdArr[i] = cmd.get(i);
        }

        return execute(cmdArr);
    }

    public void executeBatch(List<String[]> batch) {

        for (String[] strings : batch) {
            execute(strings);
        }

    }


    public class TwoBr {
        public BufferedReader output;
        public BufferedReader error;
    }
}
