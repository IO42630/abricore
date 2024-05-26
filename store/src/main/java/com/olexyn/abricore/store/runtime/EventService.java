package com.olexyn.abricore.store.runtime;

import com.olexyn.abricore.store.dao.EventDao;
import com.olexyn.min.log.LogU;
import com.olexyn.propconf.PropConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

@Service
public class EventService  {

    private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");
    private final EventDao eventDao;


    @Autowired
    public EventService(
        EventDao eventDao
    ) {
        this.eventDao = eventDao;
    }

    public int describeInt(String topic) {
        var value = describe(topic);
        if (INT_PATTERN.matcher(value).matches()) {
            return Integer.parseInt(value);
        }
        return Integer.MIN_VALUE;
    }

    public long describeLong(String topic) {
        var value = describe(topic);
        if (INT_PATTERN.matcher(value).matches()) {
            return Long.parseLong(value);
        }
        return Integer.MIN_VALUE;
    }


    public String describe(String topic)  {
        String  event = eventDao.get(topic);
        if (event != null) {
            return event;
        }
        String desc  = PropConf.get(topic);
        if (!desc.isEmpty()) {
            return desc;
        }

        switch (topic) {
            case "tmp":
                LogU.infoPlain("status:");
                var pathStr = PropConf.get("quotes.dir.tmp");
                var path = Path.of(pathStr);
                try (var list = Files.list(path)) {
                    long entries = list.count();
                    if (entries == 0) {
                        desc = topic + " is empty.";
                    } else {
                        desc = topic + " contains " + entries + " files";
                    }
                } catch (IOException e) {
                    /* ignore */
                }
                break;
            default:
                break;
        }
        LogU.infoPlain(desc);
        return  desc;
    }
}
