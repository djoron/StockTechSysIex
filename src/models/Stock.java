/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author atlantis
 */
public class Stock {

 // xxxx a verifier les types. DOuble pour entiers, Big decimal pour float
    protected String name;
    protected String symbol;
    protected String exchange;
    protected String exchangeName;
    protected String sector;
    protected String industry;
    protected String namecorrected;
    protected String symbolcorrected;
    protected String ipo;
    protected float  bookvalue;
    protected float  shortratio;
    protected String dividendPayDate;
    protected String exDividendDate;
    protected float pricebook;
    protected float marketcap;
    protected float dividendPerShare;
    protected String dayLastUpdate; // Day software was last run to update price of stock
    protected Integer obsolete;
    protected char priceSource; // Default Price Source. Y=Yahoo. G = Google (by default)

    
    /*    AAPL,Apple Inc.,NMS,119.4,117.34,119.73,117.75,32482528,11/23/2015,9.22,21.4,4:00pm - <b>117.75</b>,1.46,11/12/2015,11/05/2015,5.58,656.50B.9.22,2.08
      0- Symbol,1- Name,2- exchange,3- Open, 4- Day Low, 5- Day High,6- Close,7- Last trade price,8- Volume
      9- Date,10- EPS,11- Book value,12- Last trade with time,13- Short ratio,14- Dividend pay date,15- Q ex-dividend date,
      16- Price/book, 17 - Market capitalization,18- Earnings per share,19 - Dividend per share
*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        namecorrected = name.replaceAll("'", " ");
        // remplace tout ' par ' '
        this.name = namecorrected;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        // Google uses ., not /
        // symbolcorrected = symbol.replaceAll("/", ".");
        this.symbol = symbol; // corrected;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
    
    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getNamecorrected() {
        return namecorrected;
    }

    public void setNamecorrected(String namecorrected) {
        this.namecorrected = namecorrected;
    }

    public String getSymbolcorrected() {
        return symbolcorrected;
    }

    public void setSymbolcorrected(String symbolcorrected) {
        this.symbolcorrected = symbolcorrected;
    }
 
    public String getIpo() {
        return ipo;
    }

    public void setIpo(String ipo) {
        this.ipo = ipo;
    }    
    
    public float getBookvalue() {
        return bookvalue;
    }

    public void setBookvalue(float bookvalue) {
        this.bookvalue = bookvalue;
    }

    public float getShortratio() {
        return shortratio;
    }

    public void setShortratio(float shortratio) {
        this.shortratio = shortratio;
    }

    public String getDividendPayDate() {
        return dividendPayDate;
    }

    public void setDividendPayDate(String dividendPayDate) {
        this.dividendPayDate = dividendPayDate;
    }
    
    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public float getPricebook() {
        return pricebook;
    }

    public void setPricebook(float pricebook) {
        this.pricebook = pricebook;
    }

    public float getMarketcap() {
        return marketcap;
    }

    public void setMarketcap(float marketcap) {
        this.marketcap = marketcap;
    }
    
    public float getDividendPerShare() {
        return dividendPerShare;
    }

    public void setDividendPerShare(float dividendpershare) {
        this.dividendPerShare = dividendpershare;
    }

    public String getDayLastUpdate() {
        return dayLastUpdate;
    }

    public void setDayLastUpdate(String dayLastUpdate) {
        this.dayLastUpdate = dayLastUpdate;
    }
    public Integer getObsolete() {
        return obsolete;
    }

    public void setObsolete(Integer obsolete) {
        this.obsolete = obsolete;
    }

    public char getPriceSource() {
        return priceSource;
    }

    public void setPriceSource(char priceSource) {
        this.priceSource = priceSource;
    }
    
}