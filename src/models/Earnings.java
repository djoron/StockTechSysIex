/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 * Model for earnings data from IEX
 * https://iextrading.com/developer/docs/#earnings
 * @author dominicj
 */
public class Earnings {

    protected String actualEPS;
    protected String consensusEPS;
    protected String estimatedEPS;
    protected String announceTime;
    protected String numberOfEstimates;
    protected String EPSSurpriseDollar;
    protected String EPSReportDate;
    protected String fiscalPeriod;
    protected String fiscalEndDate;

    public String getActualEPS() {
        return actualEPS;
    }

    public void setActualEPS(String actualEPS) {
        this.actualEPS = actualEPS;
    }

    public String getConsensusEPS() {
        return consensusEPS;
    }

    public void setConsensusEPS(String consensusEPS) {
        this.consensusEPS = consensusEPS;
    }

    public String getEstimatedEPS() {
        return estimatedEPS;
    }

    public void setEstimatedEPS(String estimatedEPS) {
        this.estimatedEPS = estimatedEPS;
    }

    public String getAnnounceTime() {
        return announceTime;
    }

    public void setAnnounceTime(String announceTime) {
        this.announceTime = announceTime;
    }

    public String getNumberOfEstimates() {
        return numberOfEstimates;
    }

    public void setNumberOfEstimates(String numberOfEstimates) {
        this.numberOfEstimates = numberOfEstimates;
    }

    public String getEPSSurpriseDollar() {
        return EPSSurpriseDollar;
    }

    public void setEPSSurpriseDollar(String EPSSurpriseDollar) {
        this.EPSSurpriseDollar = EPSSurpriseDollar;
    }

    public String getEPSReportDate() {
        return EPSReportDate;
    }

    public void setEPSReportDate(String EPSReportDate) {
        this.EPSReportDate = EPSReportDate;
    }

    public String getFiscalPeriod() {
        return fiscalPeriod;
    }

    public void setFiscalPeriod(String fiscalPeriod) {
        this.fiscalPeriod = fiscalPeriod;
    }

    public String getFiscalEndDate() {
        return fiscalEndDate;
    }

    public void setFiscalEndDate(String fiscalEndDate) {
        this.fiscalEndDate = fiscalEndDate;
    }

}
