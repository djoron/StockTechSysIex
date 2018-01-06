/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for dividends data from IEX
 * https://iextrading.com/developer/docs/#dividends
 * @author dominicj
 */
public class Dividends {
   
    protected String exDate;
    protected String paymentDate;
    protected String recordDate;
    protected String declaredDate;
    protected String amount;
    @JsonProperty("type")
    protected String typediv;
    protected String qualified;

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getDeclaredDate() {
        return declaredDate;
    }

    public void setDeclaredDate(String declaredDate) {
        this.declaredDate = declaredDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTypediv() {
        return typediv;
    }

    public void setTypediv(String typediv) {
        this.typediv = typediv;
    }

    public String getQualified() {
        return qualified;
    }

    public void setQualified(String qualified) {
        this.qualified = qualified;
    }

    
}
