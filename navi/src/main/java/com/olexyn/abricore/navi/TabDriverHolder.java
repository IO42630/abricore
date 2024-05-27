package com.olexyn.abricore.navi;

import com.olexyn.tabdriver.TabDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Lazy
public class TabDriverHolder {


    private final AbricoreTabDriverConfigProvider tdConfig;
    private TabDriver td = null;
    private final Object lock = new Object();

    @Autowired
    public TabDriverHolder(AbricoreTabDriverConfigProvider tdConfig) {
        this.tdConfig = tdConfig;
    }

    public TabDriver getTd() {
        synchronized(lock) {
            if (td == null) {
                td = new TabDriver(tdConfig);
            }
            return td;
        }

    }

    public synchronized Optional<TabDriver> getTdOpt() {
        return Optional.ofNullable(td);
    }


}
