/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.StockTechSys.logger;
import dao.SqliteDaoImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static javafx.application.Platform.exit;
import models.Stock;

/**
 *
 * @author atlantis
 */
public class DatabaseService {

    static public Connection c=null;
    static public SqliteDaoImpl sqlDatabase = new SqliteDaoImpl();

    public DatabaseService() throws SQLException {
 
    }

        /**
     * Initialize database as well as all necessary tables.
     * @throws SQLException
     */
    public Boolean initializeDatabase () throws SQLException {
        
        Boolean status;
        
        // First open and create tables in DB
        logger.debug("Creating and Opening Database.");    
        openDatabase();   
        createStocklistDb();
        createStocklistTemporaryDb();
        createStocklistTrimDb();
        createStockPriceDailyDb();
        closeDatabase();
        
        return true;
    }

     /** 
     * Calls appropriate Open Database method 
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public static boolean openDatabase() throws SQLException { 
        
        boolean status=false;
        
        status = sqlDatabase.openStockDb(c);
        
        return status;
    }

         /** 
     * Calls appropriate Open Database method 
     * @version 1.0 
     * @author : dj
     * @param sqlDbDao : Database DAO object
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public static boolean closeDatabase() throws SQLException { 
        
        boolean status=false;
        
        status = sqlDatabase.closeStockDb(c);
        
        return status;
    }

        
     /** 
     *
     * Create Stock database 
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     */
    public static boolean createStocklistDb () throws SQLException { 
//        stmt = null;
//        stmt = c.createStatement();

        sqlstrb=allocateStringBuilder(sqlstrb,1000);
        logger.info("Starting from scratch database STOCK");   
        sqlstrb.append( // FOR NOW delete table if already exists
                        "DROP TABLE IF EXISTS STOCK;" +
                        "CREATE TABLE STOCK " +
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
                        "CREATE UNIQUE INDEX SYMBOL_IDX ON STOCK(SYMBOL,EXCHANGE);"
                    );

        if (commitStatementStockDb(sqlstrb)== true) {
            logger.info("createStocklist: Created STOCK table successfully");
            sqlstrb.delete(0, sqlstrb.length());
            return true;    
        } else {
            logger.error("createStocklist: Could not create table STOCK. Exiting");
            sqlstrb.delete(0, sqlstrb.length());
            closeDatabase();
            exit();
            return false;
        }
    }

    /**
     * Create StockPriceDaily Database.
     * Contains daily prices, high, low, volume, etc
     * @param c
     * @return
     * @throws SQLException 
     */
    public boolean createStockPriceDailyDb () throws SQLException { 
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
                           "FOREIGN KEY(SYMBOL,EXCHANGE) REFERENCES STOCK(SYMBOL,EXCHANGE)"+
 //                          "FOREIGN KEY(EXCHANGE) REFERENCES STOCK(EXCHANGE)"+
                           ");" +
                           "CREATE INDEX DATE_IDX ON STOCKPRICEDAILY(SYMBOL,EXCHANGE, DATE);"
            );

        if (commitStatementStockDb(sqlstrb)== true) {
            logger.info("createStockPriceDailyDb: Created table STOCKPRICEDAILY successfully");
            sqlstrb.delete(0, sqlstrb.length());
            return true;
        } else {
            logger.error("createStockPriceDailyDb: Could not create table STOCKPRICEDAILY. Exiting");
            sqlstrb.delete(0, sqlstrb.length());
            closeDatabase();
            exit();
            return false;
        }

    }

    /**
     * Create temporary Database in SQL. Used to manipulate data faster and easier
     * through DB. 
     * @param c 
     * @return true if successful
     * @throws SQLException 
     */
    
    public static boolean createStocklistTemporaryDb () throws SQLException { 
//        stmt = null;
//        stmt = c.createStatement();
        
        sqlstrb=allocateStringBuilder(sqlstrb,1000);
        logger.info("Starting from scratch database STOCKLISTTEMPORARY");   
        sqlstrb.append( // FOR NOW delete table if already exists
                        "DROP TABLE IF EXISTS STOCKLISTTEMPORARY;" +
                        "CREATE TABLE STOCKLISTTEMPORARY " +
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
            logger.info("createStocklistTemporaryDb: Created table STOCKLISTTEMPORARY successfully");
            sqlstrb.delete(0, sqlstrb.length());
            return true;    
        } else {
            logger.error("createStocklistTemporaryDb: Could not create table STOCKLISTTEMPORARY. Exiting");
            sqlstrb.delete(0, sqlstrb.length());
            closeDatabase();
            exit();
            return false;
        }
        
    }

    /** 
     *
     * Create Stock Price Daily DB 
     * Creates Daily Stock Price database.
     * @version 1.0 
     * @author : dj
     * @param c
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public static boolean createStocklistTrimDb () throws SQLException { 
        stmt = null;
        stmt = c.createStatement();
        
        sqlstrb=allocateStringBuilder(sqlstrb,1000);      
        sqlstrb.append(  "DROP TABLE IF EXISTS STOCKLISTTRIM;" +
                         "CREATE TABLE STOCKLISTTRIM " +
                         "(SYMBOL   VARCHAR(10) NOT NULL," +
                         "NAME      TEXT NOT NULL       , " +
                         "EXCHANGE  VARCHAR(10) NOT NULL " +
                         ",PRIMARY KEY (SYMBOL, EXCHANGE) ON CONFLICT IGNORE" +
                         ");"
            );
          
        if (commitStatementStockDb(sqlstrb)== true) {
            logger.info("createStocklistTrimDb: Created table STOCKLISTTRIM successfully");
            sqlstrb.delete(0, sqlstrb.length());
            return true;    
        } else {
            logger.error("createStocklistTrimDb: Could not create table STOCKLISTTRIM. Exiting");
            sqlstrb.delete(0, sqlstrb.length());
            closeDatabase();
            exit();
            return false;
        }
    }

       /**
    * addSectorListtoDb
    * Update the sector of each stock based on the list received as parameter.
    * @param stockSectorList - List of stock with their sectors
    * @return True if successful
    * @throws SQLException 
    */
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


}
