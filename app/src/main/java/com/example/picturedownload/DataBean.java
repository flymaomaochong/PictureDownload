package com.example.picturedownload;

/**
 * Created by sxj on 2019/10/7.
 */
public class DataBean {
    private String netUrl;
    private String localUrl;
    public DataBean(String netUrl,String localUrl) {
        this.netUrl = netUrl;
        this.localUrl = localUrl;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }
}
