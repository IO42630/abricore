package com.olexyn.abricore.flow;

import com.olexyn.propconf.PropConf;
import org.junit.Before;

public class FlowContextInitializer {

    @Before
    public void init() {
        PropConf.load("config.properties");
    }

}
