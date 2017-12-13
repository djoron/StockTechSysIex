/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 * Model to download price History from IEX
 * Example of call: https://api.iextrading.com/1.0/stock/aapl/chart/5y
 * Typical response:
 * {"date":"2012-12-12",
 *  "open":78.2528,
 *  "high":78.2856,
 *  "low":76.6099,
 *  "close":76.9999,
 *  "volume":121650522,
 *  "unadjustedVolume":17378646,
 *  "change":-0.341143,
 *  "changePercent":-0.441,
 *  "vwap":77.2481,
 *  "label":"Dec 12, 12",
 *  "changeOverTime":0}
 * 
 * @author dominicj
 */
public class Chart {
 
    protected String date;
    protected String open;
    protected String high;
    protected String low;
    protected String close;
    protected String volume;
    protected String unadjustedVolume;
    protected String change;
    protected String changePercent;
    protected String vwap;
    protected String label;
    protected String changeOverTime;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getUnadjustedVolume() {
        return unadjustedVolume;
    }

    public void setUnadjustedVolume(String unadjustedVolume) {
        this.unadjustedVolume = unadjustedVolume;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getVwap() {
        return vwap;
    }

    public void setVwap(String vwap) {
        this.vwap = vwap;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getChangeOverTime() {
        return changeOverTime;
    }

    public void setChangeOverTime(String changeOverTime) {
        this.changeOverTime = changeOverTime;
    }
   
   
}
