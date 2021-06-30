package com.olexyn.abricore.fingers;

public class Tab {

    private final String handle;
    private String name;
    private String url;
    private TabPurpose purpose;

    public Tab(String handle) {
        this.handle = handle;
    }


    public String getHandle() {
        return handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TabPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(TabPurpose purpose) {
        this.purpose = purpose;
    }
}
