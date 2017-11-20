/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.StockPriceDaoImpl;
import StockTechScan.Parameters;
import static StockTechScan.StockTechScan.DAILY;
import static StockTechScan.StockTechScan.logger;
import dao.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Stock;
import models.StockGoogle;
import models.StockPrice;
import utilities.ProgressBar;

/**
 *
 * @author atlantis
 */
public class PriceHistoryService {

StockPriceDaoImpl databaseService;


    public PriceHistoryService() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PriceHistoryService(DatabaseService databaseService) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Calls various methods to download price history from the internet and
     * save it in the db.
     * @return 
     */
    public boolean downloadFullDailyPriceHistoryandSavetoDb()  throws MalformedURLException, TimeoutException, RuntimeException, Exception {
        boolean status = false;
      
// xxx period parameter removed. To check later        
        
/*    // Will contain Entire Stock List to/from SQLDB.
        List<Stock> stocklist = new ArrayList<>(); 
        databaseService.loadStocklistFromDb(stocklist);
        try {  // period = Parameters.YEAR_HISTORY_STRING
            downloadPriceHistoryandSaveToDb(period, DAILY, stocklist);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MissingResourceException ex) {
            java.util.logging.Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return status;
        */

        List<StockPrice> stockPriceList = new ArrayList<>(); 
        
        int id1=0;
        int length;
        int errorCount=0;
        String symbol;     
        String exchange;
        int totalSymbol;
        TemporalAccessor t; 
        LocalDateTime ldt; 
        String timestamp=null;

        List<Stock> stocklist = new ArrayList<>(); 
        databaseService.loadStocklistFromDb(stocklist);
        
        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stocklist.size();
        pbar.CreateProgressBarStockList(totalSymbol,
                                        "Build Brand new Database with "+String.valueOf(totalSymbol)+" symbols and "+
                                         String.valueOf(Parameters.YEAR_HISTORY_INT)+" Year history. Be patient, this will take a while.");
        
        LocalDateTime startTimeEntireDailyDB = LocalDateTime.now();
        // Keep DayLastUpdate into stock object info to confirm when stock
        // price history was last updated.
        String dayLastUpdate = startTimeEntireDailyDB.toString();
        for (Stock s: stocklist) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeEntireDailyDB);

            symbol = s.getSymbol();
            exchange = s.getExchange();

            int rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
            Thread.sleep(rand);
            try {
                stockPriceHistoryDao.downloadStockPriceHistory(Parameters.YEAR_HISTORY_STRING, DAILY, symbol, stockPriceList);
            } catch (MalformedURLException ex) {
                   Logger.getLogger("downloadFullPriceHistoryandSavetoDb: MalformedURLException: {}",PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TimeoutException ex) {
                     Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RuntimeException ex){
                    logger.error("downloadFullPriceHistoryandSavetoDb exception ex {}- Skipping Symbol: {}",ex,symbol); 
            } catch (Exception ex) {
                    Logger.getLogger(PriceHistoryService.class.getName()).log(Level.SEVERE, null, ex);
                errorCount++;
                continue;
            }

            if (errorCount > 0) {
               logger.error("saveEntireDailyStockListPricetoDbComplete Total Error:{}",errorCount);
            }
            databaseService.saveStockPriceHistorytoDb(databaseService, s, stockPriceList);            

            // Price info added for stock. Now update DayLastUpdate field in
            // stock object for future reference
//              UpdateDayLastUpdate(dayLastUpdate, symbol);

            if (id1 > Parameters.MAXSTOCKTOPROCESS ) break; 
        } // For stock            
//                c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

        return true;          
    };
    
    
    /**
     * save saveLastDayStockListPricetoDb 
 Updates last day of trading prices of stockList.
     * @version 1.0
     * @author : dj
     * @param stockList - List containing Stock objects from DB.
     * @return true if successful.
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.net.MalformedURLException
     */
    public boolean saveLastDayStockListPricetoDb (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<StockPrice> stockPriceDaily = new ArrayList<>(); 
        
        int id1=0; // Index for first scan of stock symbol reading
        int totalFetchInPass=0; // Total number of stock to fetch daily price per pass
        int totalFetch=0; // Total number of stock fetched overall
        int rand = 0; // Random Sleep timer to fool Google
        double resultF;
        String symbol;      
        String exchange;
        
        int errorCount = 0;
        int totalSymbol;
        // Holds Last day stockprice. Index must match stocklist Symbol since stockprice doesn't contain symbol name.
        List<StockGoogle> lastDayStockPriceList = new ArrayList<>();

        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();
        
        StringBuilder symbolListSb = new StringBuilder("");
        
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol, "Downloading Last Day Price Data");
        
        //   LocalDateTime startTimeGoogleDailyHis = LocalDateTime.now();
        // Keep DayLastUpdate into stock object info to confirm when stock
        // price history was last updated.
        //    String dayLastUpdate = startTimeGoogleDailyHis.toString();
         for (Stock s: stockList) {    
            id1++;
            if (id1 > Parameters.MAXSTOCKTOPROCESS ) break;
            // Add , if not first symbol of chain
            // xxx Bad logic. Will miss last iteration. Could use collectors maybe ?
            if ((id1 >1 ) && (id1%Parameters.GOOGLEMAXSTOCKATATIME != 1) ) {
               symbolListSb.append(",");            
            }
            symbol = s.getSymbol();
            exchange = s.getExchange();
            // Special case here. For some reason a symbol for both US and Vancouver returned
            // Vancouver price. Will add exchange such as NASDAQ:AAPL to fix this.
            // xxx Symbols with exchange INDEXTSI don't match original name
  //          if (exchange.contains("BATS") || exchange.contains("NASDAQ") || exchange.contains("NYSE") ||
 //               exchange.contains("OTCMKTS")) {
            exchange=exchange.concat(":");
            symbol = exchange.concat(symbol);
 //           }     
            // check last day ? 
            symbolListSb.append(symbol);            
            LocalDateTime startTimeDailyHis = LocalDateTime.now();
            // Download up to GOOGLEMAXSTOCKATATIME at a time
            // Check also if last element
            if ( (id1%Parameters.GOOGLEMAXSTOCKATATIME == 0) || (id1 == totalSymbol)) { 
                try {  
                    rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
                    Thread.sleep(rand);
                    totalFetchInPass = stockPriceHistoryDao.downloadLastDayStockHistory(symbolListSb,lastDayStockPriceList);
                    totalFetch =  totalFetch+totalFetchInPass;
                    symbolListSb.setLength(0);
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    logger.debug("saveLastDayStockListPricetoDb: Total Fetched in pass {} / Total Fetched so far {}  / Total Symbols {}",totalFetchInPass,totalFetch,id1);
                } catch (Exception ex) {
                //    Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    logger.error("saveLastDayStockListPricetoDb: Skipping iteration, attempting to recover");
                    errorCount++;
                    continue;
                }
            } // if id == 0

         } // For stock  
        if (errorCount > 0) {
            logger.error("saveLastDayStockListPricetoDb: Total Error:{}",errorCount);
        }
        ProgressBar.CloseProgressBar();

        databaseService.saveLastDayStockPriceHistorytoDb(databaseService, lastDayStockPriceList);            

        logger.info("saveLastDayStockListPricetoDb: Saving Last Day Data in DB...Done");
       
        return true;                  
    }           

        /**
     * save saveLastDayStockListGooglePricetoDb 
     *  Updates last day of trading prices of stockList.
     * Also saved other useful information that is not provided by google in stocklist (Dividends, etc).
     * @version 1.0
     * @author : dj
     * @param stockList - List containing Stock objects from DB.
     * @return true if successful.
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.net.MalformedURLException
     */
/*    public static boolean saveLastDayStockListYahooDatatoDb (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<StockPriceYahoo> stockPriceDaily = new ArrayList<>(); 
        List<StockYahoo> stockYahoo = new ArrayList<>(); 
        
        int id1=0; // Index for first scan of stock symbol reading
        int id2=0; // Index for writing stock symbol into database.
        int totalFetchInPass=0; // Total number of stock to fetch daily price per pass
        int totalFetch=0; // Total number of stock fetched overall
        int rand = 0; // Random Sleep timer to fool Google
        double resultF;
        String symbol;
        String yahooSymbol;
        String exchange;
        
        int errorCount = 0;
        int totalSymbol;
        // Holds Last day stockprice. Index must match stocklist Symbol and exchange since stockprice doesn't contain symbol name.
        List<StockPriceYahoo> lastDayStockPriceListYahoo = new ArrayList<>();
        
        StringBuilder symbolListSb = new StringBuilder("");
        
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol, "saveLastDayStockListYahooDatatoDb: Downloading Last Day Price Data");
        
        //   LocalDateTime startTimeGoogleDailyHis = LocalDateTime.now();
        // Keep DayLastUpdate into stock object info to confirm when stock
        // price history was last updated.
        //    String dayLastUpdate = startTimeGoogleDailyHis.toString();
         for (Stock s: stockList) {    
             
            symbol = s.getSymbol();
            exchange = s.getExchange();
            // Will not process these stocks on yahoo.
            if ( (exchange.contains("BATS")) || (exchange.contains("CNSX")) || exchange.contains("INDEX") || exchange.contains("MUTF")  ) {
               continue;
            }
            id1++;
            if (id1 > MAXSTOCKTOPROCESS ) break;
            // Add , if not first symbol of chain
            // xxx Bad logic. Will miss last iteration. Could use collectors maybe ?
            if ((id1 >1 ) && (id1%GOOGLEMAXSTOCKATATIME != 1) ) {
               symbolListSb.append(",");            
            }
            yahooSymbol = s.getSymbol();
            
/*             // Canadian stock special rule on Yahoo
            // xxx if HSM.S.TO convert to HSM-T.to. Same rule for Vanvouver put back .V 
            if (exchange.contains("TSE") ) {
                yahooSymbol = symbol.replace(".TO", "");
                yahooSymbol = yahooSymbol.replace(".","-");
                yahooSymbol = yahooSymbol.concat(".TO");
                symbol = yahooSymbol;
            }
            if (exchange.contains("CVE")) {
                yahooSymbol = yahooSymbol.replace(".V", "");
                yahooSymbol = yahooSymbol.replace(".","-");
                yahooSymbol = yahooSymbol.concat(".V");
                symbol = yahooSymbol;
            }
* /
            symbolListSb.append(yahooSymbol);            
            LocalDateTime startTimeDailyHis = LocalDateTime.now();
            // Download up to GOOGLEMAXSTOCKATATIME at a time
            // Check also if last element
            if ( (id1%GOOGLEMAXSTOCKATATIME == 0) || (id1 == totalSymbol)) { 
                try {  
                    rand = (int)(Math.random()*TIMERANDOMDELAY);
                    Thread.sleep(rand);
                    totalFetchInPass = DownloadYahooLastDayStockHistory (symbolListSb,lastDayStockPriceListYahoo);
                    totalFetch =  totalFetch+totalFetchInPass;
                    symbolListSb.setLength(0);
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    logger.debug("saveLastDayStockListYahooDatatoDb: Total Fetched in pass {} / Total Fetched so far {}  / Total Symbols {}",totalFetchInPass,totalFetch,id1);
                } catch (Exception ex) {
                //    Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    logger.error("saveLastDayStockListYahooDatatoDb:Skipping iteration, attempting to recover");
                    errorCount++;
                    continue;
                }
            } // if id == 0
        } // For stock  
        if (errorCount > 0) {
            logger.error("saveLastDayStockListYahooDatatoDb: Total Error:{}",errorCount);
        }
        ProgressBar.CloseProgressBar();
        c.setAutoCommit(false);          
        // Use ignore since last data could have been saved with full history depending at
        // what time/day it was done
/*        prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                           "(SYMBOL, EXCHANGE, DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                           " VALUES (?,?,?,?,?,?,?,?,?,?,?);");
* /
        prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                           "(SYMBOL, EXCHANGE, DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                           " VALUES (?,?,?,?,?,?,?,?,?,?,?);");
        String str;
        logger.info("saveLastDayStockListYahooDatatoDb: Saving Last Day Data in DB");
        id2=0;
        for (StockPriceYahoo spl: lastDayStockPriceListYahoo) {
            id2++;
            // logger.info("Time-Date {} Symbol {} Op {} Hi {} Lo {} Cl {} Volume {}",spl.getLt_dts(),spl.getT(),spl.getOp(),spl.getHi(),spl.getLo(),spl.getL(),spl.getVo() );

            // Symbol
            prepStmt.setString(1,spl.getSymbol()); 
            // Exchange
            prepStmt.setString(2,spl.getExchange());
            // Get Date-Time
            str = spl.getLt_dts(); 
            if ((str != "") && (str.length() > 19) ) {
                // Remove - in 2016-04-01
                str = spl.getLt_dts().substring(0,10).replace("-", "");
                prepStmt.setString(3,str); 
                // Remove : in 12:00
                str = spl.getLt_dts().substring(11,19).replace(":",""); // Get Time
                prepStmt.setString(4,str);
            } else { // Should not happen. Log error
                prepStmt.setString(3,null);
                prepStmt.setString(4,null);
                logger.error ("saveLastDayStockListYahooDatatoDb: Could not process valid Date and time {}-{}-{}",spl.getT(), spl.getE(), spl.getLt_dts());
            }
            // Extract timestamp like 2016-03-15T07:13:36Z     2016-03-16T16:32:50Z
            if (spl.getLt_dts().isEmpty()) {
                prepStmt.setString(5,"");
            } else {
                TemporalAccessor t  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz").parse(spl.getLt_dts());
                LocalDateTime   ldt = LocalDateTime.from(t);
                // System.out.println(ldt.toEpochSecond(ZoneOffset.UTC));
                prepStmt.setString(5,Long.toString(ldt.toEpochSecond(ZoneOffset.UTC)));
            }
            // Open price
            str = spl.getOp();
            if (str != "") {
                str = spl.getOp().replace(",", "");
                prepStmt.setString(6,str);
            } else prepStmt.setString(6,null);
            // High
            str = spl.getHi();
            if (str != "") {
                str = spl.getHi().replace(",", "");
                prepStmt.setString(7,str);
            } else prepStmt.setString(7,null);
            // Low
            str = spl.getLo();
            if (str != "") {
                str = spl.getLo().replace(",", "");
                prepStmt.setString(8,str);
            } else prepStmt.setString(8,null);
            // Close price
            str = spl.getL();
            if (str != "") {
                str = spl.getL().replace(",", "");
                prepStmt.setString(9,str);
            } else prepStmt.setString(9,null);

            str = spl.getVo();
            if ( (str != null) && (str != "") ) {
                // NEED TO REMOVE LETTER XXXX
                double multiplier=1;
                str = str.replace(",", "");
                switch (str.charAt(str.length()-1)) {
                    case 'M': {
                            multiplier = (double) 1.00e6;
                            str = str.substring(0,str.length()-1);       
                    }
                    break;
                       
                    case 'B': {
                            multiplier = (double) 1.00e9;
                            str = str.substring(0,str.length()-1);
                        }
                    case 'T': {
                            multiplier = (double) 1.00e12;
                            str = str.substring(0,str.length()-1);
                        }
                    break;
                    
                    default: {
                        
                    }
                    break;    
                }
                // xxx to check. round not perfect.
                resultF = Math.round(parseDouble(str)*multiplier);
                prepStmt.setString(10,Double.toString(resultF));
            } else prepStmt.setString(10,null);
            // Add empty EPS for now
            prepStmt.setString(10,"");
            prepStmt.addBatch();
            // addStockPriceStatementtoStockdb(symbol, spl, id2, sqlstrb);
            // sqlstrb.append(",");
        } 
        id2=0;
        lastDayStockPriceListYahoo.clear(); // Without this, memory leak !!
        try  {
             prepStmt.executeBatch();
             c.commit(); 
//                     logger.info("Adding LastDayPrice {}-{}  ", id1,symbol );
        } catch ( Exception e ) {
           logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
        } 
        prepStmt.close();
        
        c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

        logger.info("saveLastDayStockListYahooDatatoDb: Saving Last Day Data in DB...Done");
       
        return true;                  

    }           
*/

        /**
     * save saveLastDayStockListYahooPricetoDb 
 Updates last day of trading prices of stockList.
     * @version 1.0
     * @author : dj
     * @param stockList - List containing Stock objects from DB.
     * @return true if successful.
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.net.MalformedURLException
     * @throws java.util.concurrent.TimeoutException
     
    public boolean saveLastDayStockListYahooPricetoDb (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<StockPriceDb> stockPriceDaily = new ArrayList<>(); 
        
        int id1=0; // Index for first scan of stock symbol reading
        int id2=0; // Index for writing stock symbol into database.
        int totalFetchInPass=0; // Total number of stock to fetch daily price per pass
        int totalFetch=0; // Total number of stock fetched overall
        int rand = 0; // Random Sleep timer to fool Google
        double resultF;
        String symbol;      
        String exchange;
        
        int errorCount = 0;
        int totalSymbol;
        // Holds Last day stockprice. Index must match stocklist Symbol since stockprice doesn't contain symbol name.
        List<StockPriceYahoo> lastDayStockPriceListYahoo = new ArrayList<>();
        
        StringBuilder symbolListSb = new StringBuilder("");
        
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol, "Yahoo Downloading Last Day Price Data");
        
        //   LocalDateTime startTimeGoogleDailyHis = LocalDateTime.now();
        // Keep DayLastUpdate into stock object info to confirm when stock
        // price history was last updated.
        //    String dayLastUpdate = startTimeGoogleDailyHis.toString();
         for (Stock s: stockList) {    
            id1++;
            if (id1 > Parameters.MAXSTOCKTOPROCESS ) break;
            // Add , if not first symbol of chain
            // xxx Bad logic. Will miss last iteration. Could use collectors maybe ?
            if ((id1 >1 ) && (id1%Parameters.YAHOOMAXSTOCKATATIME != 1) ) {
               symbolListSb.append(",");            
            }
            String sy = s.getSymbol();
            String ex = s.getExchange();
     
            // check last day ? 
            symbolListSb.append(sy);            
            LocalDateTime startTimeDailyHis = LocalDateTime.now();
            // Download up to YAHOOMAXSTOCKATATIME at a time
            // Check also if last element
            if ( (id1%Parameters.YAHOOMAXSTOCKATATIME == 0) || (id1 == totalSymbol)) { 
                try {  
                    rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
                    Thread.sleep(rand);
                    totalFetchInPass = DownloadYahooLastDayStockHistory (symbolListSb,lastDayStockPriceListYahoo);
                    totalFetch =  totalFetch+totalFetchInPass;
                    symbolListSb.setLength(0);
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    logger.debug("saveLastDayStockListYahooPricetoDb: Total Fetched in pass {} / Total Fetched so far {}  / Total Symbols {}",totalFetchInPass,totalFetch,id1);
                } catch (InterruptedException | IOException | TimeoutException | MissingResourceException exept) {
                //    Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    logger.error("saveLastDayStockListYahooPricetoDb: Skipping iteration, attempting to recover");
                    errorCount++;
                    continue;
                }
            } // if id == 0
        } // For stock  
        if (errorCount > 0) {
            logger.error("saveLastDayStockListYahooPricetoDb: Total Error:{}",errorCount);
        }
/*
        ProgressBar.CloseProgressBar();
        c.setAutoCommit(false);          
        // Use ignore since last data could have been saved with full history depending at
        // what time/day it was done
        prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                           "(SYMBOL, EXCHANGE, DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                           " VALUES (?,?,?,?,?,?,?,?,?,?,?);");

        String str;
        logger.info("saveLastDayStockListGooglePricetoDb: Saving Last Day Data in DB");
        id2=0;
        for (StockGoogle spl: lastDayStockPriceListYahoo) {
            id2++;
            // logger.info("Time-Date {} Symbol {} Op {} Hi {} Lo {} Cl {} Volume {}",spl.getLt_dts(),spl.getT(),spl.getOp(),spl.getHi(),spl.getLo(),spl.getL(),spl.getVo() );

            // Symbol
            prepStmt.setString(1,spl.getT()); 
            // Exchange
            prepStmt.setString(2,spl.getE());
            // Get Date-Time
            str = spl.getLt_dts(); 
            if ((str != "") && (str.length() > 19) ) {
                // Remove - in 2016-04-01
                str = spl.getLt_dts().substring(0,10).replace("-", "");
                prepStmt.setString(3,str); 
                // Remove : in 12:00
                str = spl.getLt_dts().substring(11,19).replace(":",""); // Get Time
                prepStmt.setString(4,str);
            } else { // Should not happen. Log error
                prepStmt.setString(3,null);
                prepStmt.setString(4,null);
                logger.error ("saveLastDayStockListGooglePricetoDb: Could not process valid Date and time {}-{}-{}",spl.getT(), spl.getE(), spl.getLt_dts());
            }
            // Extract timestamp like 2016-03-15T07:13:36Z     2016-03-16T16:32:50Z
            if (spl.getLt_dts().isEmpty()) {
                prepStmt.setString(5,"");
            } else {
                TemporalAccessor t  = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz").parse(spl.getLt_dts());
                LocalDateTime   ldt = LocalDateTime.from(t);
                // System.out.println(ldt.toEpochSecond(ZoneOffset.UTC));
                prepStmt.setString(5,Long.toString(ldt.toEpochSecond(ZoneOffset.UTC)));
            }
            // Open price
            str = spl.getOp();
            if (str != "") {
                str = spl.getOp().replace(",", "");
                prepStmt.setString(6,str);
            } else prepStmt.setString(6,null);
            // High
            str = spl.getHi();
            if (str != "") {
                str = spl.getHi().replace(",", "");
                prepStmt.setString(7,str);
            } else prepStmt.setString(7,null);
            // Low
            str = spl.getLo();
            if (str != "") {
                str = spl.getLo().replace(",", "");
                prepStmt.setString(8,str);
            } else prepStmt.setString(8,null);
            // Close price
            str = spl.getL();
            if (str != "") {
                str = spl.getL().replace(",", "");
                prepStmt.setString(9,str);
            } else prepStmt.setString(9,null);

            str = spl.getVo();
            if ( (str != null) && (str != "") ) {
                // NEED TO REMOVE LETTER XXXX
                double multiplier=1;
                str = str.replace(",", "");
                switch (str.charAt(str.length()-1)) {
                    case 'M': {
                            multiplier = (double) 1.00e6;
                            str = str.substring(0,str.length()-1);       
                    }
                    break;
                       
                    case 'B': {
                            multiplier = (double) 1.00e9;
                            str = str.substring(0,str.length()-1);
                        }
                    case 'T': {
                            multiplier = (double) 1.00e12;
                            str = str.substring(0,str.length()-1);
                        }
                    break;
                    
                    default: {
                        
                    }
                    break;    
                }
                // xxx to check. round not perfect.
                resultF = Math.round(parseDouble(str)*multiplier);
                prepStmt.setString(10,Double.toString(resultF));
            } else prepStmt.setString(10,null);
            // Add empty EPS for now
            prepStmt.setString(10,"");
            prepStmt.addBatch();
            // addStockPriceStatementtoStockdb(symbol, spl, id2, sqlstrb);
            // sqlstrb.append(",");
        } 
        id2=0;
        lastDayStockPriceListGoogle.clear(); // Without this, memory leak !!
        try  {
             prepStmt.executeBatch();
             c.commit(); 
//                     logger.info("Adding LastDayPrice {}-{}  ", id1,symbol );
        } catch ( Exception e ) {
           logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
        } 
        prepStmt.close();
        
        c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

        logger.info("saveLastDayStockListGooglePricetoDb: Saving Last Day Data in DB...Done");
       
        return true;                  
    }           
*/
    
/**    
    /* Download missing dates other than last trading day and previously downloaded data.
     * @param interval
     * @param stockList: List of stock to scan and update.
     * @return 
     * @throws java.sql.SQLException 
     * @throws java.io.IOException 
     * @throws java.lang.Exception
     * @throws java.lang.InterruptedException
     * 
**/
    public boolean UpdateMissingDailyHistoryToDb (int interval, List<Stock> stockList) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

        List<StockPrice> stockPriceList = new ArrayList<>(); 
        
        boolean completeHistory = false;
        int id1=0;
        int id2=0;
        String symbol;     
        String exchange;
        String symbolLastTradingDate;
        String symbolBeforeLastTradingDate;
        String symbolLastdateFromDb;
        String todayDateStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        int totalSymbol;
        TemporalAccessor t; 
        // Use Java 8 Localdate to manipulate dates to compare DB dates and internet
        LocalDate lastTradingDate; 
        LocalDate lastDateFromDb;
        LocalDate furtherPastDate;
        long daysBetween; // Days between LocalDates above

        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();

        StockDaoImpl stockDao;
        stockDao = new StockDaoImpl();

        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol,
                                        "Checking and updating Missing Daily history for "+String.valueOf(totalSymbol)+
                                        " symbols. Be patient, this could take a while.");

          //        for entire stock list
          // Get current latest timestamp from stock
          // Compare with today's Date.
          // If latest date is different than last available marker date, then update
          // http://www.google.com/finance/getprices?f=d,c,v,o,h,l&i=86400&p=7d&&q=AAPL
          // &p=7 jours sera calcule a partir de la soustraction des dates
                
        LocalDateTime startTimeDailyHis = LocalDateTime.now();
        furtherPastDate = LocalDate.now().minusYears(Parameters.YEAR_HISTORY_INT);
        todayDateStr = LocalDateTime.now().toString().replace("-", "");
        lastDateFromDb = LocalDate.now(); // Serves to initialize. Changed later.
        
        // Assume SYMBOLTOCHECKLASTMARKETOPENDATE contains latest Trading Date for all stocks.
        // This will save numerous calls to Google in the for loop below
        symbolLastTradingDate = stockPriceHistoryDao.getLastOpenMarketDate(Parameters.SYMBOLTOCHECKLASTMARKETOPENDATE);
        // https://www.google.com/finance/getprices?f=d,c,v,o,h,l&i=86400&p=2d&q=AAPL
        // Use this to check if more than 1 day update is required.
        symbolBeforeLastTradingDate = stockPriceHistoryDao.getBeforeLastOpenMarketDate(Parameters.SYMBOLTOCHECKLASTMARKETOPENDATE);
        
        for (Stock s: stockList) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                       
            symbol = s.getSymbol();
            exchange = s.getExchange();
            // Get last date (yyyyMMdd) for symbol from local priceDB
            symbolLastdateFromDb = stockDao.findLatestSymbolDatefromDb(symbol,exchange);
            completeHistory = false;
            if (symbolLastdateFromDb.isEmpty()) {
                logger.info("UpdateMissingDailyHistoryToDb: Symbol {}-{}-{} has no date avail in DB. Skip for now.",id1,symbol,exchange );
                continue; // XXX should we continue ? 
            }
            // Save time. If symbolLastdateFromDb equals last trading day,
            // no need to update.
            if (symbolLastdateFromDb.equalsIgnoreCase(symbolLastTradingDate)) {
                // logger.info("UpdateMissingDailyHistoryToDb: Symbol {}-{}-{}. Already up-to-date: {} ", id1,symbol,exchange,symbolLastdateFromDb);
                continue;
            }

            if (symbolLastdateFromDb.equalsIgnoreCase(symbolBeforeLastTradingDate)) {
                logger.info("UpdateMissingDailyHistoryToDb: Symbol {}-{}-{} only need last day update. Will do in DownloadGoogleLastDayStockHistory",
                            id1,symbol,exchange );
                continue;
            }
            
            // Manipulate LocalDate objects to check how many days we need to update
            lastTradingDate = LocalDate.parse(symbolLastTradingDate,formatter);
  
            if (completeHistory == true) {
              // If no history in DB, then go for full update
                lastDateFromDb.equals(furtherPastDate);
                completeHistory = false;
            } else {
                if ( (symbolLastdateFromDb == null) || (symbolLastdateFromDb.equals("")) ) {
                    lastDateFromDb.equals(furtherPastDate);
                } else
                lastDateFromDb = LocalDate.parse(symbolLastdateFromDb,formatter);
            }
            // Adding 1 by default. If we're here, the dates don't match so we need
            // to update 1 day at least.
            daysBetween = DAYS.between(lastDateFromDb, lastTradingDate) + 1; 

            int rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
            Thread.sleep(rand);
            // period was YEAR_HISTORY_STRING...will need to change xxx
            stockPriceHistoryDao.downloadStockPriceHistory( String.valueOf(daysBetween),DAILY, symbol, stockPriceList); 

            if (stockPriceList.isEmpty()) {
                logger.info("UpdateMissingDailyHistoryToDb: No Data to save {}-{}  ", id1,symbol );
                continue;
            } else {
                databaseService.saveStockPriceHistorytoDb(databaseService, s, stockPriceList); 
            }
        } // For Stock s:...

//                c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

    return true;          
    }    
    /**
     * Call DAO method to return last open market date for given symbol
     * @param symbol
     * @return 
     */
    public String getLastOpenMarketDate (String symbol) throws Exception
    {
        GooglePriceHistoryDaoImpl priceHistoryDao = new GooglePriceHistoryDaoImpl();
        
        String date;
        date = priceHistoryDao.getLastOpenMarketDate(symbol);
        return date;
        
    };
    
    
        
    /**    
     * Check last day symbol price is available. If more than an amount of days
     * make it obsolete in the database.
     * Assume latest data has been downloaded. Simply check the last date in
     * local db.
     * @param stockList: List of stock to scan and update.
     * @return : True if any obsolete is found
     * 
    **/
    public boolean CheckObsoleteSymbol (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

        List<Stock> stockListObsolete = new ArrayList<>(); 

        String symbol;     
        String exchange;
        int totalSymbol;
        long timestamp;
        int id1=0;
        int id2=0; // Number of symbols obsolete
        int id3=0;
        // Use Java 8 Localdate to manipulate dates to compare DB dates and internet
        LocalDate lastDateFromDb=null;
                
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol,"Checking for obsolete symbols if no data available for "+String.valueOf(Parameters.MAXDAYSBEFOREOBSOLETE)+" days");
        logger.info("Checking for obsolete symbols.");
        LocalDateTime startTimeDailyHis = LocalDateTime.now();

        StockDao stockDao = new StockDaoImpl();
        
        // First scan stocklist for obsolete and add them to a 
        // stockListObsolete list
        for (Stock s: stockList) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                       
            symbol = s.getSymbol();
            exchange = s.getExchange();

            // Get last date (yyyyMMdd) for symbol from local priceDB
            timestamp = stockDao.findLatestTimestampDatefromDb(symbol,exchange);
// xxx to fix returns null            symbolBeforeLastDateFromDb = findSymbolBeforeLastDatefromDb(symbol);

            lastDateFromDb = LocalDate.ofEpochDay((long)(timestamp/86400));
            long daysBetween = DAYS.between(lastDateFromDb,startTimeDailyHis.toLocalDate() );
            
            if (daysBetween <  Parameters.MAXDAYSBEFOREOBSOLETE) {
                // Not enough days yet to be obsolete.
                continue;
            } 

            id2++;
            Stock stockObs = new Stock();
            stockObs.setSymbol  (symbol);
            stockObs.setExchange (exchange);
            stockListObsolete.add(stockObs);
            logger.info("CheckObsoleteSymbol: Obsolete {}-{}-{}. DaysBetween: {} ", id1,symbol,exchange,daysBetween);

            // From here the stock has become obsolete. Specify it in DB.

        } // For Stock s:...
        
        // If we found obsolete, mark them in local DB here.
        if (id2>0) {
            Connection c = null;
            PreparedStatement prepStmt = null;
        
            SqliteDaoImpl sqlDatabase = new SqliteDaoImpl();
            openDatabase();

            c.setAutoCommit(false);
            prepStmt = c.prepareStatement("UPDATE STOCK " +
                "SET OBSOLETE=? "+
                "WHERE SYMBOL = ? AND EXCHANGE = ?");

            for (Stock slo: stockListObsolete) {
                id3++;
                prepStmt.setString(1,"1"); // 1 means obsolete. Null or 0 otherwise
                prepStmt.setString(2,slo.getSymbol());
                prepStmt.setString(3,slo.getExchange());
                prepStmt.addBatch();
            }
            try {
                logger.info("CheckObsoleteSymbol: Applying to local database. Symbols marked:{}",id3);
                prepStmt.executeBatch();
                c.commit(); 
            } catch ( Exception e ) {
               logger.error("CheckObsoleteSymbol: {} : {}",e.getClass().getName(),e.getMessage() );
               closeDatabase();
            } 
            prepStmt.close();
            closeDatabase();
            c.setAutoCommit(true);          

        }
        ProgressBar.CloseProgressBar();
        logger.info("Total Obsolete Symbols found: {}",id2); 
        stockListObsolete.clear();

        return true;          
    }      

    
    /**    
    /* Check for stock splits by scanning % difference between the before last
     * and last price. Download full history if split suspected.
     * Use a difference of 30% (3 for 2 split) or more to download data.
     * 
     * Assume data has been downloaded and DB is updated with latest price.
     * @param stockList: List of stock to scan and update.
     * 
**//* xxx Review this stocksplit */

/*
    public boolean CheckForStockSplit (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

        List<StockPriceDb> stockPriceList = new ArrayList<>(); 
        
        String symbol;     
        String exchange;
        String symbolLastDateFromDb;
        int totalSymbol;
        int id1=0;
        int days = 2; // Check if stock split occured in the last 2 days (today and yesterday).
        float lastDayClosingPrice=0;
        float beforeLastDayClosingPrice=0;
        float percentageDifference=0;
        TemporalAccessor t; 
        String symbolLastTradingDate;
        String symbolBeforeLastTradingDate;
        // Use Java 8 Localdate to manipulate dates to compare DB dates and internet
        LocalDate lastTradingDate; 
        String lastDateFromDb;
        LocalDate furtherPastDate;
        String todayDateStr;

        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();

        
        StockDaoImpl stockDao;
        stockDao = new StockDaoImpl();

        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol,"Checking for stock splits by comparing last 2 day prices. Re-download stock history if %diff is >"+String.valueOf(Parameters.SPLITPERCENTAGEDETECT)+"%");

        LocalDateTime startTimeDailyHis = LocalDateTime.now();
        for (Stock s: stockList) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                       
            symbol = s.getSymbol();
            exchange = s.getExchange();
            // Skip OTCMKTS symbols
            if (exchange.contains("OTCMKTS")) { 
                continue; 
            }

            // Make sure is is cleared before calling getSymbolPricefromDb
            stockPriceList.clear();
            // Get closing price of 2 days.
            if (stockDao.getSymbolPriceFromDb(symbol,exchange, days, stockPriceList) != days) {
                // Should equal the same days as called else skip;
                continue;    
            }       
            lastDayClosingPrice = 0;
            beforeLastDayClosingPrice = 0;
            // Expect 2 days here.
            if (stockPriceList.size() == days) {
                lastDayClosingPrice = Float.parseFloat(stockPriceList.get(0).getClose());
                beforeLastDayClosingPrice = Float.parseFloat(stockPriceList.get(1).getClose());
            } else continue;
            
            if ( (lastDayClosingPrice < Parameters.MINPRICEFORSPLITDETECT) && (beforeLastDayClosingPrice< Parameters.MINPRICEFORSPLITDETECT)) 
            {  // If price is too low, do not check for split. Can cause to many false positives
                continue;
            
            }   
            // Calculate % difference
            if (lastDayClosingPrice >= beforeLastDayClosingPrice) {
                percentageDifference = (lastDayClosingPrice-beforeLastDayClosingPrice) *100 / beforeLastDayClosingPrice;
            } else
            {
                percentageDifference = (beforeLastDayClosingPrice-lastDayClosingPrice) *100 / lastDayClosingPrice;
            }
            // Clear it now since could be used later to download complete data
            lastDateFromDb = stockPriceList.get(0).getDate(); 

            stockPriceList.clear();
            
            if (percentageDifference < Parameters.SPLITPERCENTAGEDETECT) {
                // skip symbol and move to next
                continue; 
            } else {

                startTimeDailyHis = LocalDateTime.now();
                furtherPastDate = LocalDate.now().minusYears(Parameters.YEAR_HISTORY_INT);
                todayDateStr = LocalDateTime.now().toString().replace("-", "");
                
                int rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
                Thread.sleep(rand);
                // xxx to change ! so we calculate from first date available in DB to today.
                stockPriceHistoryDao.downloadStockPriceHistory(Parameters.YEAR_HISTORY_STRING,DAILY, symbol, stockPriceList); 

                if (stockPriceList.isEmpty()) {
                    logger.info("UpdateMissingDailyHistoryToDb: No Data to save {}-{}  ", id1,symbol );
                    continue;
                } else {
                    // Insert Fetched data into db
                    c.setAutoCommit(false);

                    // ConvertStockPriceListToPreparedStatement(prepStmt, stockPriceList);
/*                         prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                               "(SYMBOL ,DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                               " VALUES (?,?,?,?,?,?,?,?,?,?);");

                    // Don't change EPS since no data here.

                    prepStmt = c.prepareStatement("UPDATE STOCKPRICEDAILY " +
                           "SET TIME=?, TIMESTAMP=?, OPEN=?, HIGH=?, LOW=?, CLOSE=?, VOLUME=? "+
                           "WHERE SYMBOL = ? AND DATE = ? AND EXCHANGE = ?");

                    for (StockPrice spl: stockPriceList) {
                        prepStmt.setString(1,spl.getTime());
                        prepStmt.setString(2,spl.getTimestamp());
                        prepStmt.setString(3,spl.getOpen());
                        prepStmt.setString(4,spl.getHigh());
                        prepStmt.setString(5,spl.getLow());
                        prepStmt.setString(6,spl.getClose());
                        prepStmt.setString(7,spl.getVolume());
                        prepStmt.setString(8,symbol);
                        prepStmt.setString(9,spl.getDate());
                        prepStmt.setString(10,exchange);
                        prepStmt.addBatch();
                        // addStockPriceStatementtoStockdb(symbol, spl, id2, sqlstrb);
                        // sqlstrb.append(",");
                    } 
                    stockPriceList.clear(); // Without this, memory leak !!
                    try  {
                         prepStmt.executeBatch();
                         c.commit(); 
                         logger.info("CheckForStockSplit: Split detected {}-{}-{}. LastDate: {} Price before {}. After {}, Percentage {}", id1,symbol,exchange,
                                                                 lastDateFromDb, lastDayClosingPrice, beforeLastDayClosingPrice, percentageDifference);
                    } catch ( Exception e ) {
                       logger.error("CheckForStockSplit: {} : {}",e.getClass().getName(),e.getMessage() );
                    } 
                    prepStmt.close();
                }
            }
        } // For Stock s:...

        c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

    return true;          
    }           
*/

}
