package com.olexyn.abricore.datasets;


import org.json.JSONObject;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public class App {

    private static Tools tools = new Tools();

    public static void main(String[] args) {
        System.out.println("Hello World!");


        String configPath = System.getProperty("user.dir") + "/datasets/src/main/resources/alphavantage-ibm-fullsample.json";
        String configString = tools.fileToString(new File(configPath));

        JSONObject rootJson = new JSONObject(configString);

        String metaDataKey = tools.getMatchingJsonKeys(rootJson, "meta data").get(0);
        JSONObject metaDataJson = rootJson.getJSONObject(metaDataKey);


        String zoneDataKey = tools.getMatchingJsonKeys(metaDataJson, "time zone").get(0);
        String zoneDataString = metaDataJson.getString(zoneDataKey);



        JSONObject jsonObject = rootJson.getJSONObject("Time Series (5min)");
        Set<String> keyset = jsonObject.keySet();

        Map<ZonedDateTime, BarDto> map = new HashMap<>();

        for (String key : keyset){
            JSONObject jsonBar = jsonObject.getJSONObject(key);

            BarDto barDto = new BarDto();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(key + " " + zoneDataString, dateTimeFormatter);
            barDto.setZonedDateTime(zonedDateTime);
            barDto.setOpen(tools.getFirstMatchingAsDouble(jsonBar, "open"));
            barDto.setHigh(tools.getFirstMatchingAsDouble(jsonBar, "high"));
            barDto.setLow(tools.getFirstMatchingAsDouble(jsonBar, "low"));
            barDto.setClose(tools.getFirstMatchingAsDouble(jsonBar, "close"));
            barDto.setVolume(tools.getFirstMatchingAsDouble(jsonBar, "volume"));




            map.put(zonedDateTime, barDto);
        }


        //    .getJSONObject("jsonMapOfSyncMaps");
        // or (String key : jsonMapOfSyncMaps.keySet()) {
        //    SyncMap syncMap = new SyncMap(key);
        //    for (Object jsonSyncDirPath : jsonMapOfSyncMaps.getJSONArray(key).toList()) {
        //        syncMap.addDirectory(jsonSyncDirPath.toString());
        //    }
        //    MapOfSyncMaps.get().put(key, syncMap);
        //
        int br = 0;
    }
}
