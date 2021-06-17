package com.olexyn.abricore.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {

    private final Execute x;

    public FileUtil() {
        x = new Execute();
    }

    public String brToString(BufferedReader br) {
        StringBuilder sb = new StringBuilder();
        Object[] br_array = br.lines().toArray();
        for (int i = 0; i < br_array.length; i++) {
            sb.append(br_array[i].toString() + "\n");
        }
        return sb.toString();
    }

    public String catFile(String path) {
        BufferedReader output = x.execute(new String[]{"cat",
            path}).output;
        return brToString(output);
    }

    public List<String> fileToLines(File file) {
        String filePath = file.getPath();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public String fileToString(File file){
        List<String> lineList = fileToLines(file);
        StringBuilder sb = new StringBuilder();
        for (String line : lineList){
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

}
