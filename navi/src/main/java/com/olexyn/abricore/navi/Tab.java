package com.olexyn.abricore.navi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tab {

    private String handle;
    private String name;
    private String url;
    private TabPurpose purpose;


    public Tab(String handle) {
        this.handle = handle;
    }

}
