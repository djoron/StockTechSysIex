
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import StockTechSys.Parameters;
import static StockTechSys.StockTechSys.DAILY;
import static StockTechSys.StockTechSys.logger;
import static externalData.GooglePrice.*;
import externalData.GooglePriceHistory;
import static externalData.GooglePriceHistory.GetGoogleBeforeLastOpenMarketDate;
import java.io.File;
import utilities.ProgressBar;
import java.io.IOException;
import static java.lang.Double.parseDouble;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;  
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atlantis
 */
public class SqlDatabase {

    public static String databaseFileName = "Stock_Database.db";
    private static Connection c=null;
    private static Statement stmt = null;
    private static PreparedStatement prepStmt = null;
    public static StringBuilder sqlstrb;
    private static StringBuilder connectionName;
    
// private static PreparedStatement pstmt=null;
 
    public static StringBuilder AllocateStringBuilder(StringBuilder sqlstrb, int size) {
        // To be used to build price list
        return new StringBuilder(size);
    }

    public static boolean checkStockDbExist () {
        File f = new File(databaseFileName);

      if(f.exists()){
          return true;
      }else{
          return false; 
      }                
    }
    
    public static boolean deleteStockDb () {
        boolean bool;
        File f = new File(databaseFileName);
        bool = f.delete();
        return bool;                
    }
    /** 
     *
     * Opens SQL Stock database 
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     */
    public static boolean openStockDb ()  {

        try {
          Class.forName("org.sqlite.JDBC");
          connectionName = new StringBuilder("jdbc:sqlite:"+databaseFileName);
          c = DriverManager.getConnection(connectionName.toString());
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          logger.debug("FAILED opening database. No connection made. Exiting...");
          System.exit(0);
          return false;
        } 
//        connectionName.setLength(0);
        logger.debug("Opened database successfully");
        return true;
    } 
    
    /** 
     *
     * Create Stock database 
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     */
    public static boolean createStockListDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
       // !!!! to correct drop table if exists
        sqlstrb=AllocateStringBuilder(sqlstrb,1000);
        logger.info("Starting from scratch database STOCKLIST");   
        sqlstrb.append( // FOR NOW delete table if already exists
                        "DROP TABLE IF EXISTS STOCKLIST;" +
                        "CREATE TABLE STOCKLIST " +
                        //                      "(ID INT PRIMARY KEY    NOT NULL," +
//                        "(SYMBOL VARCHAR(10) PRIMARY KEY  NOT NULL," +
                        "(SYMBOL             VARCHAR(10) NOT NULL," +                        
                        " NAME               TEXT NOT NULL, " +
                        " EXCHANGE           VARCHAR(10), " +
                        " EXCHANGENAME       TEXT, " +
                        " SECTOR             TEXT, " +
                        " INDUSTRY           TEXT, " + 
                        " IPO                VARCHAR(12), " + 
                        " BOOKVALUE          LONG, " +
                        " SHORTRATIO         LONG, " +
                        " DIVIDENDPAYDATE    VARCHAR(12), " +
                        " EXDIVIDENDPAYDATE  VARCHAR(12), " +
                        " PRICEBOOK          LONG, " +
                        " MARKETCAP          LONG, " +
                        " DIVIDENDPERSHARE   LONG, " +                                
                        " TAG1               LONG, " +
                        " TAG2               LONG,  " +
                        " DAYLASTUPDATE      VARCHAR(24), " + // Last day price info fetched
                        " OBSOLETE           INTEGER  " +
//                        ",PRIMARY KEY (SYMBOL, EXCHANGE) ON CONFLICT IGNORE" +
//                      ",UNIQUE (SYMBOL, EXCHANGE) ON CONFLICT REPLACE" +
                        ",UNIQUE (SYMBOL, exchange) ON CONFLICT IGNORE " +
                        ");" +
                        "CREATE UNIQUE INDEX SYMBOL_IDX ON STOCKLIST(SYMBOL,EXCHANGE);"
                    );
       
        if (commitStatementStockDb(sqlstrb)== true) {
           logger.info("createStockListDb: Opened database STOCKLIST successfully");
           sqlstrb.delete(0, sqlstrb.length());
           return true;    
        } else {
           logger.error("createStockListDb: Error opening database STOCKLIST");   
           sqlstrb.delete(0, sqlstrb.length());
           return false;
        }
    }

        // Remplace createStockListBloombergDb
        public static boolean createStockListBloombergDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
       // !!!! to correct drop table if exists
        sqlstrb=AllocateStringBuilder(sqlstrb,1000);
        logger.info("Starting from scratch database STOCKLISTBLOOMBERG");   
        sqlstrb.append( // FOR NOW delete table if already exists
                        "DROP TABLE IF EXISTS STOCKLISTBLOOMBERG;" +
                        "CREATE TABLE STOCKLISTBLOOMBERG " +
                        //                      "(ID INT PRIMARY KEY    NOT NULL," +
                        "(SYMBOL VARCHAR(10) NOT NULL," +   // primary key
                        " NAME               TEXT NOT NULL, " +
                        " EXCHANGE           VARCHAR(10), " +
                        " SECTOR             TEXT, " +
                        " INDUSTRY           TEXT " + 
                        ",PRIMARY KEY (SYMBOL, EXCHANGE) ON CONFLICT IGNORE" +
                        // " UNIQUE (SYMBOL) ON CONFLICT FAIL" +
                        ");"
        );
        
        if (commitStatementStockDb(sqlstrb)== true) {
           logger.info("Opened database STOCKLISTBLOOMBERG successfully");
           sqlstrb.delete(0, sqlstrb.length());
           return true;    
        } else {
           logger.error("Error opening database STOCKLISTBLOOMBERG");   
           sqlstrb.delete(0, sqlstrb.length());
           return false;
        }
    }
    /** 
     *
     * Create Stock Price Daily DB 
     * Creates Daily Stock Price database.
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     */
    public static boolean createStockListTrimDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
        
        sqlstrb=AllocateStringBuilder(sqlstrb,1000);      
        sqlstrb.append(  "DROP TABLE IF EXISTS STOCKLISTTRIM;" +
                         "CREATE TABLE STOCKLISTTRIM " +
                         "(SYMBOL   VARCHAR(10) NOT NULL," +
                         "NAME      TEXT NOT NULL       , " +
                         "EXCHANGE  VARCHAR(10) NOT NULL " +
                         ",PRIMARY KEY (SYMBOL, EXCHANGE) ON CONFLICT IGNORE" +
                         ");"
            );
          
        if (commitStatementStockDb(sqlstrb)== true) {
           logger.info("Table StockListTrim created successfully");
        
           sqlstrb.delete(0, sqlstrb.length());
            return true;
        } else return false;
    }

    public static boolean deleteDuplicateFromStockListDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
        
        sqlstrb.append(
                         "DELETE FROM STOCKLIST " +
                         "WHERE ROWID NOT IN "+
                         "("+
                         "SELECT MIN (ROWID) "+
                         "FROM STOCKLIST " +
                         "GROUP BY SYMBOL, EXCHANGE)"
             );
            
        if (commitStatementStockDb(sqlstrb)== true) {
             logger.info("Removed Duplicates from StockList");
             sqlstrb.delete(0, sqlstrb.length());
             return true;
        } else {
             logger.info("ERROR removing Duplicates from StockList");
             sqlstrb.delete(0, sqlstrb.length());
             return false;
        }
    }

        public static boolean deleteDuplicateFromStockListTrimDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
        
        sqlstrb.append(
                         "DELETE FROM STOCKLISTTRIM " +
                         "WHERE ROWID NOT IN "+
                         "("+
                         "SELECT MIN (ROWID) "+
                         "FROM STOCKLISTTRIM " +
                         "GROUP BY SYMBOL, EXCHANGE)"
             );
            
        if (commitStatementStockDb(sqlstrb)== true) {
             logger.info("Removed Duplicates from StockListTrim");
             sqlstrb.delete(0, sqlstrb.length());
             return true;
        } else {
             logger.info("ERROR removing Duplicates from StockListTrim");
             sqlstrb.delete(0, sqlstrb.length());
             return false;
        }
    }

    /** 
     *
     * Stocklisttrim could contain stocks (obolete) already in database.
     * Send SQL command to delete from stocklisttrim any stock already in stocklist
     * @author : dj
     * @return true if successful.
     */

public static boolean deleteFromStockListTrimDbDuplicatesInStocklist () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
        
        sqlstrb.append(
                         "DELETE FROM stocklisttrim " +
                         "WHERE EXISTS (SELECT SYMBOL,EXCHANGE FROM stocklist "+
                         "Where stocklisttrim.symbol = stocklist.symbol and "+
                         "stocklisttrim.exchange = stocklist.exchange )"
                      );
            
        if (commitStatementStockDb(sqlstrb)== true) {
             logger.info("Removed Duplicates (obsolete) from StockListTrim found in stocklist");
             sqlstrb.delete(0, sqlstrb.length());
             return true;
        } else {
             logger.info("ERROR removing Duplicates (obsolete) from StockListTrim found in stocklist");
             sqlstrb.delete(0, sqlstrb.length());
             return false;
        }
    }

    public static boolean createStockPriceDailyDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
//            sqlstrb = new StringBuilder(
        sqlstrb.append(
                           "DROP TABLE IF EXISTS STOCKPRICEDAILY;" +
                           "CREATE TABLE STOCKPRICEDAILY " +
                           "(SYMBOL     VARCHAR(10) NOT NULL," +
                           " EXCHANGE   VARCHAR(10) NOT NULL, " +
                           " DATE       VARCHAR(12) NOT NULL ," + 
                           " TIME       VARCHAR(10) NOT NULL," + 
                           " TIMESTAMP  VARCHAR(11), " +
                           " OPEN       VARCHAR(14), " + 
                           " HIGH       VARCHAR(14), " + 
                           " LOW        VARCHAR(14), " + 
                           " CLOSE      VARCHAR(14), " +
                           " VOLUME     VARCHAR(20), " + 
                           " EPS        VARCHAR(6), " +    
                           "UNIQUE (SYMBOL, EXCHANGE, DATE) ON CONFLICT REPLACE, " +
                           "FOREIGN KEY(SYMBOL,EXCHANGE) REFERENCES STOCKLIST(SYMBOL,EXCHANGE)"+
 //                          "FOREIGN KEY(EXCHANGE) REFERENCES STOCKLIST(EXCHANGE)"+
                           ");" +
                           "CREATE INDEX DATE_IDX ON STOCKPRICEDAILY(SYMBOL,EXCHANGE, DATE);"
            );
          
        if (commitStatementStockDb(sqlstrb)== true) {
 //          logger.info("Table StockPriceDb {} created successfully",symbol);
             sqlstrb.delete(0, sqlstrb.length());
             return true;
        } else {
             sqlstrb.delete(0, sqlstrb.length());
             return false;
        }
    }

   /** 
     *
     * Delete records with name, symbol or exchange of 0 length
     * Added this since Google returned AG&E Holdings with no symbol or exchange.
     * Symbol does look valid on stockharts thought. Will need to check this later.
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     */
    public static boolean sendTrimCommandToDb () throws SQLException { 
// 
        stmt = null;
        stmt = c.createStatement();
//            sqlstrb = new StringBuilder(
        sqlstrb.append("DELETE FROM STOCKLIST WHERE"+
                       "(LENGTH(SYMBOL)==0 OR LENGTH(NAME)==0 OR LENGTH(EXCHANGE)==0)"
                      );
          
        if (commitStatementStockDb(sqlstrb)== true) {
           logger.info("sendTrimCommandtoDb: Stocklist Trimmed successfully");
        
           sqlstrb.delete(0, sqlstrb.length());
            return true;
        } else {
            logger.info("sendTrimCommandtoDb: SQL command returned error");
            return false;
        }
    }

    public static boolean addStocklisttoDb (List<Stock> stockList) throws SQLException {
        
        if (stockList.size() > 0) {
            stmt = null;
            stmt = c.createStatement();
            c.setAutoCommit(false);

            prepStmt = c.prepareStatement("INSERT INTO STOCKLIST (SYMBOL ,NAME,EXCHANGE) VALUES (?,?,?);");
            for (Stock s: stockList) {
                prepStmt.setString(1,s.getSymbol());
                prepStmt.setString(2,s.getName());
                prepStmt.setString(3,s.getExchange());
                prepStmt.addBatch();
            }

            try  {
                prepStmt.executeBatch();
                c.commit(); 
            } catch ( Exception e ) {
                logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                return false;
            } 
            prepStmt.close();
            c.setAutoCommit(true);          
            logger.info("Added Stocklist...Done");
        } else {
            logger.info("No Stock to add");
            
        }
        return true;          
    }

    public static boolean addStocklisttoBloombergDb (List<Stock> stockList) throws SQLException {
        stmt = null;
        stmt = c.createStatement();

        c.setAutoCommit(false);

        prepStmt = c.prepareStatement("INSERT INTO STOCKLISTBLOOMBERG (SYMBOL ,NAME,EXCHANGE) VALUES (?,?,?);");
        for (Stock s: stockList) {
            prepStmt.setString(1,s.getSymbol());
            prepStmt.setString(2,s.getName());
            prepStmt.setString(3,s.getExchange());
            prepStmt.addBatch();
        }
  
        try  {
            prepStmt.executeBatch();
            c.commit(); 
        } catch ( Exception e ) {
            logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
            return false;
        } 
        prepStmt.close();
        c.setAutoCommit(true);          
        logger.info("Added StocklistBloomberg...Done");
        return true;          
    }
    
    /** 
     *
     * LoadStocklistFromDb 
     * Load Stock list from Master DB and save in StockList list. Only load stocks
     * that are not obsolete.
          * @version 2.0 
     * @author : dj
     * @return true if successful.
     */     
    public static boolean loadStocklistFromDb (List<Stock> stockList) throws SQLException {

        logger.info("Loading Stocklist from DB ... Standby");

        stockList.clear(); // Just to be sure
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKLIST WHERE OBSOLETE IS NOT ?"
        );
                       
        prepStmt.setString(1,"1");
           
        ResultSet rs = prepStmt.executeQuery();
        try {
            while ( rs.next() ) {
                Stock stock = new Stock();
                stock.setSymbol  (rs.getString("SYMBOL"));
                stock.setName    (rs.getString("NAME"));
                stock.setExchange (rs.getString("EXCHANGE"));
                stock.setSector (rs.getString("SECTOR"));
                stock.setIndustry (rs.getString("INDUSTRY"));
                stock.setIpo (rs.getString("IPO"));
                stock.setDayLastUpdate (rs.getString("DAYLASTUPDATE"));
                stockList.add(stock);
            }
        prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
        }
        logger.info("Loaded Stocklist from DB ... Done");
        return true;
    }

        /** 
     *
     * LoadStocklistFromDb 
     * Load Stock list from Master DB and save in StockList list.
          * @version 1.0 
     * @author : dj
     * @return true if successful.
     */     
    public static boolean loadStocklistFromTrimStockListDb (List<Stock> stockList) throws SQLException {
                
        String sym = null;
        String dat = null;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKLISTTRIM;"
        );
                       
                
        ResultSet rs = prepStmt.executeQuery();
        try {
            while ( rs.next() ) {
                Stock stock = new Stock();
                stock.setSymbol  (rs.getString("SYMBOL"));
                stock.setName    (rs.getString("NAME"));
                stock.setExchange (rs.getString("EXCHANGE"));
                // stock.setSector (rs.getString("SECTOR"));
                // stock.setIndustry (rs.getString("INDUSTRY"));
                // stock.setIpo (rs.getString("IPO"));
                // stock.setDayLastUpdate (rs.getString("DAYLASTUPDATE"));
                stockList.add(stock);
            }
        prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
        }
        logger.info("Loaded Stocklist from StockListTrimDB ...Done");
        return true;
    }

    public static boolean addSectorListtoDb (List<Stock> stockSectorList) throws SQLException {
        stmt = null;
        stmt = c.createStatement();
        
        int id1=0;
         
        logger.info("Updating Sector Info...please wait");
        c.setAutoCommit(false);

        prepStmt = c.prepareStatement("UPDATE STOCKLIST " +
                    " SET SECTOR=?, INDUSTRY=?, IPO=?"+
                    " WHERE NAME=? OR NAME LIKE ?");

// xxx to fix. Stock such as Accuride Corporatio (ACW) doesn't work since NYSE
// names it Accuride Corporation New
        String nameUsa;
        for (Stock s: stockSectorList) {    
            id1++;
            
            // ConvertStockPriceListToPreparedStatement(prepStmt, stockPriceList);

            prepStmt.setString(1,s.getSector());
            prepStmt.setString(2,s.getIndustry());
            prepStmt.setString(3,s.getIpo());
            // Name Modification to standardize Bloomberg with Google and Local DB
            if(s.getName().endsWith(".")) {
               // Remove extra . in stock sector name from NYSE or Nasdaq. Bloomberg
               // doesn't add a .
               nameUsa = s.getName().substring(0, s.getName().length() - 1);
            } else {
                nameUsa = s.getName();
            }
            prepStmt.setString(4,nameUsa);
            prepStmt.setString(5,nameUsa+"%(USA)");
            prepStmt.addBatch();
        } 
        stockSectorList.clear(); // Clear list to avoid Memory Leak

        try  {
            prepStmt.executeBatch();
            c.commit(); 
        } catch ( Exception e ) {
            logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
            return false;
        } 
        prepStmt.close();
        c.setAutoCommit(true);          
        logger.info("Updating Sector Info...Done");
        return true;          
    }

    /** 
     *
     * saveEntireDailyStockListPricetoDbComplete
     * @version 1.0 
     * @author : dj
     * @param period : Time period to scan for
     * @param timeunit: Last minute, week, month, year available.
     * @param stockList: List of stock to scan and update.
     * @return true if successful.
     * download entire StockPrice history.
     * if lastdate is not null, download from lastdate in the timeunit requested and ignore the period.
     */

    public static boolean saveEntireDailyStockListPricetoDbComplete (String period, long timeunit,List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {
        
        List<Quote> stockPriceList = new ArrayList<>(); 
        
        int id1=0;
        int id2=0;
        int length;
        int errorCount=0;
        String symbol;     
        String exchange;
        int totalSymbol;
        TemporalAccessor t; 
        LocalDateTime ldt; 
        String timestamp=null;
        
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol,
                                        "Build Brand new Database with "+String.valueOf(totalSymbol)+" symbols and "+
                                         String.valueOf(Parameters.YEAR_HISTORY_INT)+" Year history. Be patient, this will take a while.");
        
        if (createStockPriceDailyDb() == true) {
            LocalDateTime startTimeEntireDailyDB = LocalDateTime.now();
            // Keep DayLastUpdate into stock object info to confirm when stock
            // price history was last updated.
            String dayLastUpdate = startTimeEntireDailyDB.toString();
            for (Stock s: stockList) {    
                id1++;
                ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeEntireDailyDB);

                symbol = s.getSymbol();
                exchange = s.getExchange();
 
 /*              // Prepare symbol. Not exact same format as for Last Day Google Fetch.
                // Add .TO or .V for TSX and Vancouver. Otherwise, keep symbol only
                symbol = s.getSymbol();
                exchange = s.getExchange();

                     switch (exchange.toUpperCase()) {                
                         case "TSE": {
                                 symbol=(symbol.concat(".TO"));
                         }
                         break;

                         case "CVE": {
                                 symbol=(symbol.concat(".V"));
                         }
                         break;

                         default:
                             break;
                     }

*/
                // createStockPriceDailyDb(s.getSymbol());
                // add a delay here to avoid being blocked by Google 
                int rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
                Thread.sleep(rand);
                try {
                    GooglePriceHistory.DownloadGoogleStockHistory(Parameters.YEAR_HISTORY_STRING,timeunit, symbol, stockPriceList); 
                } catch (MalformedURLException ex) {
                   Logger.getLogger("MalformedURLException: {}",GooglePriceHistory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TimeoutException ex) {
                     Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                } catch (RuntimeException ex){
                    logger.error("DownloadGoogleStockHistory exception ex {}- Skipping Symbol: {}",ex,symbol); 
                    errorCount++;
                    continue;
                }

                if (errorCount > 0) {
                   logger.error("saveEntireDailyStockListPricetoDbComplete Total Error:{}",errorCount);
                }

                // Begin
                c.setAutoCommit(false);

                // ConvertStockPriceListToPreparedStatement(prepStmt, stockPriceList);
                prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                           "(SYMBOL, EXCHANGE, DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                           " VALUES (?,?,?,?,?,?,?,?,?,?,?);");
                // For each day, a line is returned with stock price info. Build 
                // prepared statement and save into DB.               
                for (Quote spl: stockPriceList) {
                     id2++;
                     prepStmt.setString(1,symbol);
                     prepStmt.setString(2,exchange);
                     prepStmt.setString(3,spl.getDate());
                     prepStmt.setString(4,spl.getTime());
                     // Extract timestamp like 2011-03-17 16:00      2016-03-16T16:32:50Z
                     if ( spl.getDate().isEmpty() || spl.getTime().isEmpty() ) {
                         prepStmt.setString(5,"");
                     } else {
                         t  = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").parse(spl.getDate()+spl.getTime());
                         ldt = LocalDateTime.from(t);
                         timestamp = Long.toString(ldt.toEpochSecond(ZoneOffset.UTC));
                         // System.out.println(ldt.toEpochSecond(ZoneOffset.UTC));
                         prepStmt.setString(5,timestamp);
                     }                   

                     prepStmt.setString(6,spl.getOpen());
                     prepStmt.setString(7,spl.getHigh());
                     prepStmt.setString(8,spl.getLow());
                     prepStmt.setString(9,spl.getClose());
                     prepStmt.setString(10,spl.getVolume());
                     prepStmt.setString(11,"");
                     
                     prepStmt.addBatch();
                     // addStockPriceStatementtoStockdb(symbol, spl, id2, sqlstrb);
                     // sqlstrb.append(",");
                } 
                id2=0;
                stockPriceList.clear(); // Without this, memory leak !!
                try  {
                    prepStmt.executeBatch();
                    c.commit(); 
                    logger.info("saveEntireDailyStockListPricetoDbComplete: Adding {}-{}-{}  ", id1,symbol, exchange);
                } catch ( Exception e ) {
                  logger.error("saveEntireDailyStockListPricetoDbComplete: {} : {}",e.getClass().getName(),e.getMessage() );
                } 
                prepStmt.close();
                
                // Price info added for stock. Now update DayLastUpdate field in
                // stock object for future reference
  //              UpdateDayLastUpdate(dayLastUpdate, symbol);

                if (id1 > Parameters.MAXSTOCKTOPROCESS ) break; 
            } // For stock            
//                c.setAutoCommit(true);          
            ProgressBar.CloseProgressBar();
        }
        return true;          
    }           
 

    /**
     * save saveLastDayStockListGooglePricetoDb 
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
    public static boolean saveLastDayStockListGooglePricetoDb (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<Quote> stockPriceDaily = new ArrayList<>(); 
        
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
        List<StockGoogle> lastDayStockPriceListGoogle = new ArrayList<>();
        
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
                    totalFetchInPass = DownloadGoogleLastDayStockHistory (symbolListSb,lastDayStockPriceListGoogle);
                    totalFetch =  totalFetch+totalFetchInPass;
                    symbolListSb.setLength(0);
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    logger.debug("saveLastDayStockListGooglePricetoDb: Total Fetched in pass {} / Total Fetched so far {}  / Total Symbols {}",totalFetchInPass,totalFetch,id1);
                } catch (Exception ex) {
                //    Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    logger.error("saveLastDayStockListGooglePricetoDb: Skipping iteration, attempting to recover");
                    errorCount++;
                    continue;
                }
            } // if id == 0
        } // For stock  
        if (errorCount > 0) {
            logger.error("saveLastDayStockListGooglePricetoDb: Total Error:{}",errorCount);
        }
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
        for (StockGoogle spl: lastDayStockPriceListGoogle) {
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

    
/**    
    /* Download missing dates other than last trading day and previously downloaded data. 
     * @param stockList: List of stock to scan and update.
     * 
**/
    public static boolean UpdateMissingDailyHistoryToDb (int interval, List<Stock> stockList) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

        List<Quote> stockPriceList = new ArrayList<>(); 
        
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
        symbolLastTradingDate = GetGoogleLastOpenMarketDate(Parameters.SYMBOLTOCHECKLASTMARKETOPENDATE);
        // https://www.google.com/finance/getprices?f=d,c,v,o,h,l&i=86400&p=2d&q=AAPL
        // Use this to check if more than 1 day update is required.
        symbolBeforeLastTradingDate = GetGoogleBeforeLastOpenMarketDate(Parameters.SYMBOLTOCHECKLASTMARKETOPENDATE);
        
        for (Stock s: stockList) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                       
            symbol = s.getSymbol();
            exchange = s.getExchange();
            // Get last date (yyyyMMdd) for symbol from local priceDB
            symbolLastdateFromDb = findLatestSymbolDatefromDb(symbol,exchange);
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
            try {  GooglePriceHistory.DownloadGoogleStockHistory(String.valueOf(daysBetween),DAILY, symbol, stockPriceList); 
                } catch (MalformedURLException ex) {
                   Logger.getLogger("MalformedURLException: {}",GooglePriceHistory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TimeoutException ex) {
                     Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (stockPriceList.isEmpty()) {
                    logger.info("UpdateMissingDailyHistoryToDb: No Data to save {}-{}  ", id1,symbol );
                    continue;
                } else {
                    // Insert Fetched data into db
                    c.setAutoCommit(false);

                    // ConvertStockPriceListToPreparedStatement(prepStmt, stockPriceList);
                    prepStmt = c.prepareStatement("INSERT OR IGNORE INTO STOCKPRICEDAILY " +
                               "(SYMBOL, EXCHANGE, DATE, TIME, TIMESTAMP, OPEN, HIGH, LOW, CLOSE, VOLUME, EPS)"+
                               " VALUES (?,?,?,?,?,?,?,?,?,?,?);");

                    for (Quote spl: stockPriceList) {
                        id2++;
                        prepStmt.setString(1,symbol);
                        prepStmt.setString(2,exchange);
                        prepStmt.setString(3,spl.getDate());
                        prepStmt.setString(4,spl.getTime());
                        prepStmt.setString(5,spl.getTimestamp());
                        prepStmt.setString(6,spl.getOpen());
                        prepStmt.setString(7,spl.getHigh());
                        prepStmt.setString(8,spl.getLow());
                        prepStmt.setString(9,spl.getClose());
                        prepStmt.setString(10,spl.getVolume());
                        prepStmt.setString(11,"");
                        prepStmt.addBatch();
                        // addStockPriceStatementtoStockdb(symbol, spl, id2, sqlstrb);
                        // sqlstrb.append(",");
                    } 
                    id2=0;
                    stockPriceList.clear(); // Without this, memory leak !!
                    try  {
                         prepStmt.executeBatch();
                         c.commit(); 
                         logger.info("UpdateMissingDailyHistoryToDb: Adding {}-{}-{}. Number of Days Scanned: {}. Begin {}, End {}", id1,symbol,exchange,
                                                                                                        daysBetween, lastDateFromDb, lastTradingDate);
                    } catch ( Exception e ) {
                       logger.error("UpdateMissingDailyHistoryToDb: {} : {}",e.getClass().getName(),e.getMessage() );
                    } 
                    prepStmt.close();
                }
        } // For Stock s:...

//                c.setAutoCommit(true);          
        ProgressBar.CloseProgressBar();

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
**/
    public static boolean CheckForStockSplit (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

        List<Quote> stockPriceList = new ArrayList<>(); 
        
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
            if (getSymbolPriceFromDb(symbol,exchange, days, stockPriceList) != days) {
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
                try {  GooglePriceHistory.DownloadGoogleStockHistory(Parameters.YEAR_HISTORY_STRING,DAILY, symbol, stockPriceList); 
                    } catch (MalformedURLException ex) {
                       Logger.getLogger("MalformedURLException: {}",GooglePriceHistory.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TimeoutException ex) {
                         Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                    }

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
*/
                        // Don't change EPS since no data here.

                        prepStmt = c.prepareStatement("UPDATE STOCKPRICEDAILY " +
                               "SET TIME=?, TIMESTAMP=?, OPEN=?, HIGH=?, LOW=?, CLOSE=?, VOLUME=? "+
                               "WHERE SYMBOL = ? AND DATE = ? AND EXCHANGE = ?");

                        for (Quote spl: stockPriceList) {
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

        /**    
    /* Check last day symbol price is available. If more than an amount of days
     * make it obsolete in the database.
     * Assume latest data has been downloaded. Simply check the last date in
     * local db.
     * @param stockList: List of stock to scan and update.
     * @return : True if any obsolete is found
     * 
**/
    public static boolean CheckObsoleteSymbol (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, Exception {

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

        // First scan stocklist for obsolete and add them to a 
        // stockListObsolete list
        for (Stock s: stockList) {    
            id1++;
            ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                       
            symbol = s.getSymbol();
            exchange = s.getExchange();

            // Get last date (yyyyMMdd) for symbol from local priceDB
            timestamp = findLatestTimestampDatefromDb(symbol,exchange);
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
            c.setAutoCommit(false);
            prepStmt = c.prepareStatement("UPDATE STOCKLIST " +
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
            } 
            prepStmt.close();
        }

        ProgressBar.CloseProgressBar();
        logger.info("Total Obsolete Symbols found: {}",id2); 
        stockListObsolete.clear();
        c.setAutoCommit(true);          

        return true;          
    }           

    public static boolean commitStatementStockDb (StringBuilder sqlstrb) throws SQLException {

        try  {
          stmt.executeUpdate(sqlstrb.toString());
        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
        return false;
        }
        return true;
    } 
    
    
    public static String findLatestSymbolDatefromDb(String symbol, String exchange) throws SQLException {
        String sym = null;
        String dat = null;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKPRICEDAILY " +
                "WHERE SYMBOL = ? AND EXCHANGE = ? "+
                "AND DATE = (SELECT MAX(DATE) " +
                "FROM STOCKPRICEDAILY WHERE SYMBOL = ?); "
        );                       
 
        prepStmt.setString(1,symbol);
        prepStmt.setString(2,exchange);
        prepStmt.setString(3,symbol);
//        prepStmt.addBatch();
                
        ResultSet rs = prepStmt.executeQuery();
        // Initialize to avoid crash later
        dat = "";
        try {
            while ( rs.next() ) {
                sym = rs.getString("symbol");
                dat  = rs.getString("date");
//                System.out.println( "symbol = " + sym );
//                System.out.println( "Date= " + dat );
//                System.out.println();
            }
           prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
          return null;
        }
        return dat;
    }

public static Long findLatestTimestampDatefromDb(String symbol, String exchange) throws SQLException {
        String sym = null;
        String strTimestamp = null;
        Long timestamp;
        int id1=0;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKPRICEDAILY " +
                "WHERE SYMBOL = ? AND EXCHANGE = ? "+
                "AND TIMESTAMP = (SELECT MAX(TIMESTAMP) " +
                "FROM STOCKPRICEDAILY WHERE SYMBOL = ?); "
        );                       
 
        prepStmt.setString(1,symbol);
        prepStmt.setString(2,exchange);
        prepStmt.setString(3,symbol);
//        prepStmt.addBatch();
                
        ResultSet rs = prepStmt.executeQuery();
        // Initialize to avoid crash later
        strTimestamp="";
        try {
            while ( rs.next() ) {
                id1++;
                sym = rs.getString("symbol");
                strTimestamp  = rs.getString("timestamp");
            }
           prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
          return (long)0;
        }
        if (id1==0) { // No data ever recorded
            strTimestamp = "0";
        }
        timestamp = Long.parseLong(strTimestamp);
        return timestamp;
    }

    public static String findSymbolBeforeLastDatefromDb(String symbol, String exchange) throws SQLException {
        String sym = null;
        String dat = null;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKPRICEDAILY " +
                "WHERE SYMBOL = ? AND EXCHANGE = ? "+
                "AND DATE = (SELECT MAX(DATE) " +
                "FROM STOCKPRICEDAILY WHERE SYMBOL = ?); "
        );                       
 
        prepStmt.setString(1,symbol);
        prepStmt.setString(2,exchange);
        prepStmt.setString(3,symbol);
//        prepStmt.addBatch();
                
        ResultSet rs = prepStmt.executeQuery();
        // Initialize to avoid crash later
        dat = "";
        try {
            rs.next();
            while ( rs.previous() ) {
                sym = rs.getString("symbol");
                dat  = rs.getString("date");
//                System.out.println( "symbol = " + sym );
//                System.out.println( "Date= " + dat );
//                System.out.println();
            }
           prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
          return null;
        }
        return dat;
    }
    
        public static int findLatestSymbolTimestampfromDb(String symbol, String exchange) throws SQLException {
        String sym = null;
        String dat = null;
        String timestamp = null;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKPRICEDAILY " +
                "WHERE SYMBOL = ? AND EXCHANGE = ? "+
                "AND DATE = (SELECT MAX(DATE) " +
                "FROM STOCKPRICEDAILY WHERE SYMBOL = ?); "
        );                       
 
        prepStmt.setString(1,symbol);
        prepStmt.setString(2,exchange);
        prepStmt.setString(3,symbol);
//        prepStmt.addBatch();
                
        ResultSet rs = prepStmt.executeQuery();
        try {
            while ( rs.next() ) {
                sym = rs.getString("symbol");
                dat  = rs.getString("date");
                timestamp = rs.getString("timestamp");
//                System.out.println( "symbol = " + sym );
//                System.out.println( "Date= " + dat );
//                System.out.println();
            }
           prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
          return 0;
        }
        return Integer.parseInt(timestamp);
    }

/**    
    /* Retrieve from sql db the last x days of data for a given symbol.
    *  
     * Assume data has been downloaded and DB is updated with latest price.
     * @param symbol : Symbol to retrieve
     * @param days : Last x days to retrieve data
     * @param stockPriceList : Array containing data
     * @return number of dates processed
     * 
**/
     
    public static int getSymbolPriceFromDb(String symbol, String exchange, int days, List<Quote> stockPriceList) throws SQLException {
        String sym = null;
        String dat = null;
        String timestamp = null;
        String close = null;
        int processed=0;
        
        c.setAutoCommit(false);
        prepStmt = c.prepareStatement(
                "SELECT * FROM STOCKPRICEDAILY " +
                "WHERE SYMBOL = ? AND EXCHANGE = ? "+
                "GROUP BY DATE " +
                "ORDER BY DATE DESC LIMIT ?; "
        );                       
 
        prepStmt.setString(1,symbol);
        prepStmt.setString(2,exchange);
        prepStmt.setString(3,String.valueOf(days) );
                
        ResultSet rs = prepStmt.executeQuery();

        try {
            while ( rs.next() ) {
                processed++;
                Quote stockPrice = new Quote();
                stockPrice.setSymbol  (rs.getString("SYMBOL")); 
                stockPrice.setExchange(rs.getString("EXCHANGE"));
                stockPrice.setTimestamp(rs.getString("TIMESTAMP"));
                stockPrice.setDate    (rs.getString("DATE"));
                stockPrice.setOpen    (rs.getString("OPEN"));
                stockPrice.setHigh    (rs.getString("HIGH"));
                stockPrice.setLow     (rs.getString("LOW"));
                stockPrice.setClose   (rs.getString("CLOSE"));
                stockPriceList.add(stockPrice);
            }
        prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          // System.exit(0);
          return 0;
        }
        return processed;
    }

    /**
     * save TrimUpdateBloombergStockList 
     * Takes Bloomberg stocklist, and search for missing Symbol+descr in StockListBloomberg
     * If missing, add it to new stocklist which will be used later.
     * @version 1.0
     * @author : dj
     * @param bloombergstockList - List containing Stock objects from DB.
     * @param newstockList - List containing new stocks from Bloomberg
     * @return true if successful.
     * @throws java.io.Exception
     */
    public static boolean TrimUpdateBloombergStockList (List<Stock> bloombergstockList, List<Stock> newstockList  ) throws SQLException {
     
        boolean status = false;
        int id1 = 0;
        String symbol; // Stock symbol
        String name;   // Stock name

        c.setAutoCommit(false);

        // Scan bloombergstockList and check if it exists in DB.
        // If it does, just skip to next iteration.
        // If it doesn't, add symbol into newstockList for later use;
        
        for (Stock s: bloombergstockList) {    
            id1++;
            String SymbolTmp = s.getSymbol();
            if (SymbolTmp.contains("NTNX")) {
                logger.info("getBloombergStockList: NTNX found");
            }
            // This takes a long time. xxxxx 
            prepStmt = c.prepareStatement(
                    "SELECT * FROM STOCKLISTBLOOMBERG WHERE SYMBOL = ? "
                            + "AND EXCHANGE = ? ;"
            );

            // Not efficient. If AAPL already exists, will still have to scan OTC, NYSE, and etc markets with this symbol
            // But best way to detect new symbol or even old symbol with new exchange...
            prepStmt.setString(1,s.getSymbol());
//            prepStmt.setString(2,s.getName());
            prepStmt.setString(2,s.getExchange());
            ResultSet rs = prepStmt.executeQuery();
            int found = 0;
            try {
                while ( rs.next() ) {
                    // if we go in while, there is 1 element
                    found++;                
                }
                prepStmt.close();
                switch (found) {
                    case 0: // We found a new stock
                            newstockList.add(s);
                             logger.info ("TrimUpdateBloombergStockList. Potential new stock. Will check later if exists {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
                            break;
                    case 1: // logger.info ("TrimUpdateBloombergStockList Already exists {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
                            break;
                    default:// Means found twice in STOCKLISTBLOOMBERG. Possible. It gets trimmed later.
                            logger.warn("TrimUpdateBloombergStockList Found Twice {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
                            break;
                }
            } catch ( Exception e ) {
                    logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                    return false;
                    // System.exit(0);
                    // Exception
            }
                    
        }
        c.setAutoCommit(true);          
     
        logger.info("Trimmed Bloomberg last day data...Done");
        return true;
    }
    
    
    /**
     * save LastDayStockListPricetoDb 
     * Trims Stocklist and returns list containing stock symbols with valid Last Day Dat
     * from Google.
     * Use stockList to call up GoogleDailyPrice history. This will return a StockGoogle
     * object with 
     * @version 1.0
     * @author : dj
     * @param stockList - List containing Stock objects from DB.
     * @return true if successful.
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.net.MalformedURLException
     */
    public static boolean trimStockList (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<Quote> stockListTrim = new ArrayList<>(); 
        
        int id1=0; // Index for first scan of stock symbol reading
        int id2=0; // GooglePrice pass
        int totalFetchInPass=0; // Total number of stock to fetch daily price per pass
        int totalFetch=0; // Total number of stock fetched overall
        int length;
        String symbol;      
        String exchange;
        int totalSymbol;
        
        // Holds Last day stockprice. Index must match stocklist Symbol since stockprice doesn't contain symbol name.
        List<StockGoogle> lastDaystockPriceList = new ArrayList<>(); // Total list downloaded
        // Partial list containing GOOGLEMAXSTOCKATATIME stockGoogle objects.
        List<StockGoogle> lastDaystockPriceListPartial = new ArrayList<>(); 
                   
        StringBuilder symbolListSb = new StringBuilder("");
        
        // Create ProgressBar while we fill the database.
        ProgressBar pbar = new ProgressBar();
        totalSymbol = stockList.size();
        pbar.CreateProgressBarStockList(totalSymbol, "Trim Stocklist - Trim Stocklist - Downloading Last Day Price Data to trim ghost symbols from firstpass Symbol Data");
        logger.debug("Trim Stocklist - Downloading Last Day Price Data to trim ghost symbols from firstpass Symbol Data");
        LocalDateTime startTimeGoogleDailyHis = LocalDateTime.now();
        // Keep DayLastUpdate into stock object info to confirm when stock
        // price history was last updated.
        String dayLastUpdate = startTimeGoogleDailyHis.toString();
               
        for (Stock s: stockList) {    
            id1++;
           
            // Add , if not first symbol of chain
            if ((id1 >1 ) && (id1%Parameters.GOOGLEMAXSTOCKATATIME != 1)){
               symbolListSb.append(",");            
            }
            
            symbol = s.getSymbol();
 
            if (symbol.contains("NTNX")) {
                logger.info("trimStockList: NTNX found");
            }
 
            symbolListSb.append(symbol); 
            // check last day
            LocalDateTime startTimeDailyHis = LocalDateTime.now();
            // Download up to GOOGLEMAXSTOCKATATIME at a time
            if ((id1%Parameters.GOOGLEMAXSTOCKATATIME == 0) || (id1==stockList.size())) { 
            id2++;
                    // add a delay here to avoid being blocked by Google 
                    int rand = (int)(Math.random()*Parameters.TIMERANDOMDELAY);
                    Thread.sleep(rand);
                    try {
                        totalFetchInPass = DownloadGoogleLastDayStockHistory(symbolListSb,lastDaystockPriceListPartial);
                    } catch (Exception ex) {
                        Logger.getLogger(SqlDatabase.class.getName()).log(Level.SEVERE, null, ex);
                        logger.error("DownloadGoogleLastDayStockHistory error: ");
                        continue;
                    }
                    totalFetch =  totalFetch+totalFetchInPass;
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    symbolListSb.delete(0, symbolListSb.length());
                    lastDaystockPriceList.addAll(lastDaystockPriceListPartial);
                    lastDaystockPriceListPartial.clear();
                    logger.debug("Pass #{} TrimList: Total Feched in pass {} / Total Feched so far {}  / Total Symbols {}",id2,totalFetchInPass,totalFetch,id1);                
                
            }   // if        
            if (id1 > Parameters.MAXSTOCKTOPROCESS ) break;
        } // for stocks: stocklist
        logger.debug("Total Fetched {} / Total Symbols {}",totalFetch,id1);
        ProgressBar.CloseProgressBar();
        
        // We have our trim Stock list. 
        // Save temporary table in DB with fetched data from google
        // We will use this list to delete ghost symbols/

        createStockListTrimDb();
        c.setAutoCommit(false);
        
        prepStmt = c.prepareStatement("INSERT INTO STOCKLISTTRIM " +
                                      "(SYMBOL, NAME,EXCHANGE)"+
                                      " VALUES (?,?,?);");
        
        id2=0;
        for (StockGoogle ldspl: lastDaystockPriceList) {
//          int id3 = 2;
            String sy = ldspl.getT();
            String ex = ldspl.getE();
            String na = ldspl.getName();
            
            /* As of August 29th 2016, here are the exchanges returned by Google from stocklist
            "ASX"	"2"
            "BATS"	"2"
            "CNSX"	"78"
            "CVE"	"2080"
            "FRA"	"1"
            "INDEXDJX"	"1"
            "INDEXNASDAQ"	"1"
            "INDEXRUSSELL"	"1"
            "INDEXSP"	"1"
            "INDEXTSI"	"4"
            "LON"	"1"
            "MUTF_CA"	"4"
            "NASDAQ"	"2924"
            "NYSE"	"2543"
            "NYSEARCA"	"1541"
            "NYSEMKT"	"258"
            "OTCBB"	"99"
            "OTCMKTS"	"3572"
            "TSE"	"1572"
            */
            // Remove extra exchange symbols sometimes returned by Google.
            // If exchange below match, skip symbol.
            if ( ex.contains("TYO") || ex.contains("OTCBB") || ex.contains("ASX") ||
                 ex.contains("INDEXTSI") || ex.contains("OTCBB") || ex.contains("LON") ) { // || (Pattern.compile("[0-9]").matcher(sy).find())) ) { 
                 logger.info("TrimStockList: Discarding Symbol {}, exchange {}, Name {}",sy, ex, na);
                 continue;
            }
            
            // Prepare symbol. Not exact same format as for Last Day Google Fetch.
            // Add .TO or .V for TSX and Vancouver. Otherwise, keep symbol only

            switch (ex.toUpperCase()) {                
                case "TSE": {
                        sy=(sy.concat(".TO"));
                }
                break;

                case "CVE": {
                        sy=(sy.concat(".V"));
                }
                break;

                default:
                    break;
            }

            logger.info("TrimStockList: Adding new Symbol {}, exchange {}, Name {}",sy, ex, na);
            id2++;
            prepStmt.setString(1,sy);
            prepStmt.setString(2,na);
            prepStmt.setString(3,ex);
            prepStmt.addBatch();
        } 
        logger.info("Total PrepStatement {}, Size {}",id2,lastDaystockPriceList.size());
        try  {
             prepStmt.executeBatch();
             c.commit(); 
        } catch ( Exception e ) {
           logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
        } 
        logger.info("TrimStockList: Total PrepStatement ... done");
        prepStmt.close();
        lastDaystockPriceList.clear(); 
        c.setAutoCommit(true);          
        
        if (sendTrimCommandToDb() == false) {
        }
        return true;
    }
            
    public static boolean closeStockDb ()  {
        try  {
              c.close();
        } catch ( Exception e ) {
        logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
        // System.exit(0);
        return false;
        }
        logger.info("Closed StockDB successfully");
    return true;
    } 

}


/* Find duplicate rows with data from 2 columns

SELECT
    symbol, name, COUNT(*)
FROM
    stocklist
GROUP BY
    symbol, name
HAVING 
    COUNT(*) > 1


delete duplicate rows keeping only one

delete   from stocklist
where    rowid not in
         (
         select  min(rowid)
         from    stocklist
         group by
                 symbol
         ,       exchange
         )
*/


/*
Search values =10 
select * from stockpricedaily where ( ( (CAST(close as double)) = 10.0) and date = "2016-03-11")

*/

/* find last date update
SELECT
    date, COUNT(*)
FROM
    stockpricedaily
GROUP BY
    date
order by date 

*/
