package com.olexyn.abricore.navi;

import com.olexyn.abricore.util.FileUtil;
import com.olexyn.min.log.LogU;
import com.olexyn.tabdriver.TabDriver;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class Session {

    public final TabDriver td;

    public Session(TabDriver td) {
        this.td = td;
    }

    public abstract void doLogin();

    /**
     * Get credentails from JSON at Path
     */
    protected Map<String, String> extractCredentialMap(String cred_path) {
        Map<String, String> extractedMap = new HashMap<>();

        String contents = new FileUtil().fileToString(new File(cred_path));
        JSONObject obj;
        try {
            obj = new JSONObject(contents);
            for (String key : obj.keySet()) {
                extractedMap.put(key, obj.getString(key));
            }
        } catch (JSONException | NullPointerException e) {
            LogU.warnPlain(e.getMessage(), e);
        }
        return extractedMap;
    }

    protected abstract Map<String, String> fetchCredentials();

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LogU.infoPlain("Sleep interrupted.");
        }
    }

    /**
     * Ensure that the current URL is the given URL.
     */
    protected void ensureUrl(String url) {
        synchronized(td) {
            if (!td.getCurrentUrl().equals(url)) {
                td.get(url);
            }
        }
    }

}
