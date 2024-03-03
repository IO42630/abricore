package com.olexyn.abricore.util;

import com.olexyn.abricore.util.log.LogU;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private final Execute x;

    public FileUtil() {
        x = new Execute();
    }

    public String brToString(BufferedReader br) {
        StringBuilder sb = new StringBuilder();
        Object[] br_array = br.lines().toArray();
        for (Object o : br_array) {
            sb.append(o.toString()).append("\n");
        }
        return sb.toString();
    }

    public String catFile(String path) {
        BufferedReader output = x.execute(new String[]{
            "cat",
            path
        }).output;
        return brToString(output);
    }

    public List<String> fileToLines(File file) {
        String filePath = file.getPath();
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            LogU.warnPlain(e.getMessage(), e);
        }
        return lines;
    }

    public String fileToString(File file) {
        List<String> lineList = fileToLines(file);
        StringBuilder sb = new StringBuilder();
        for (String line : lineList) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static @Nullable String getHash(Path path) {
        try (var is = Files.newInputStream(path)) {
            var m = MessageDigest.getInstance("SHA256");
            byte[] buffer = new byte[262144];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                m.update(buffer, 0, bytesRead);
            }
            var i = new BigInteger(1, m.digest());
            return String.format("%1$032X", i);
        } catch (Exception e) {
            LogU.warnPlain("Failed to create Hash: %s", e.getMessage());
            return null;
        }
    }

}
