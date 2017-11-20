/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.StockTechSys.logger;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Company;
import models.Stock;
import models.StockGoogle;
import static service.DatabaseService.c;

import utilities.ProgressBar;

        
/**
 *
 * @author atlantis
 */
public class StockService {
    private DatabaseService databaseService;
    private IexSymbolDaoImpl internetStocklist;
    private StockService    stockService;


    public StockService(DatabaseService databaseService)
    {
        this.databaseService=databaseService;

    }

    public StockService() {
        throw new UnsupportedOperationException("StockService: Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    public boolean createStocklist() throws SQLException, IOException, InterruptedException, MissingResourceException, MalformedURLException, TimeoutException {
        boolean status;
         // Will contain Entire Stock List to/from SQLDB.
        // Will contain Entire Stock List from Internet.
        List<Company> companyListIex = new ArrayList<>(); 
        // Will contain Entire Stock List to/from SQLDB.
        List<Company> companyListSql = new ArrayList<>(); 

        IexDao iexDao = new IexDaoImpl();

            // Use Bloomberg to download full Stock List to populate new DB 
            if (internetStocklist.getInternetStocklist(stocklist) == 0) 
            {
                // If returns 0 no data so cannot build stocklist. Must exit
                logger.info("Could not download any data...exciting");
                exit(0);
            }
            
            // First open and create tables in DB
            logger.debug("Creating StockListDb.");

            // Add full Bloomberg stocklist so far to BloombergDb for future reference
            logger.debug("Adding StocklisttoBloombergDb for future reference.");
            status = stockDao.addStocklisttoTemporaryDb(stocklist);
            
            // Trim stockList here. Remove duplicates and symbols with no data
            // from google. This will save a stocklist in stocklisttrimDb.
            logger.debug("Trimming StockList, removing duplicate entries.");
            status = trimStockList(stocklist);
            stocklist.clear();
            
            // Load stocklistfrom DB to load proper format (symbol, name, exchange)
            // saved with trimStockList
            stockDao.loadStocklistFromTrimStocklistDb(stocklist);
            stockDao.addStocklisttoDb(stocklist);
            // These 2 last deleteDuplicateFromStockListDb will get rid of duplicates and
            // records with empty symbol, name or exchange in DB.
            stockDao.deleteDuplicateFromStocklistDb();
            stockDao.sendTrimCommandToDb();
            // Reload clean from DB
            stocklist.clear();
            stockDao.loadStocklistFromDb(stocklist);
            

        return true;
        
    }


    /** 
     *
     * getStockListFromInternet downloads from Internet a list of potential 
     * stocks could contain stocks (obsolete) already in database. List will be
     * trimmed later.
     * @author : dj
     * @param stocklist
     * @return true if successful.
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    
    public boolean getStockListFromInternet(List<Stock> stocklist) throws IOException, InterruptedException {    
    
        BloombergDaoImpl internetStockList;
        internetStockList = new BloombergDaoImpl() {};
    
        int status;
        boolean statBool = false;
        // Status return total number of symbols fetched.
        status = internetStockList.getInternetStocklist(stocklist);

        if (status == 0) { statBool = false;} else 
        {
            statBool = true;
        }

        return statBool;
  
    }
    

    /**
     * trimUpdateInternetStocklist 
     * Takes latest downloaded Internet stocklist, and search for missing Symbol+descr in StockListTemporary
     * If missing, add it to new stocklist which will be used later.
     * @version 1.0
     * @author : dj
     * @param internetStocklist - List containing Stock objects from DB.
     * @param newstockList - List containing new stocks from Bloomberg
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public static boolean trimUpdateInternetStocklist (List<Stock> internetStocklist, List<Stock> newstockList  ) throws SQLException {
     
        boolean status = false;
        int id1 = 0;
        String symbol; // Stock symbol
        String name;   // Stock name
        PreparedStatement prepStmt;
        c.setAutoCommit(false);

        // Scan bloombergstockList and check if it exists in DB.
        // If it does, just skip to next iteration.
        // If it doesn't, add symbol into newstockList for later use;
        
        for (Stock s: internetStocklist) {    
            id1++;
            String SymbolTmp = s.getSymbol();
            if (SymbolTmp.contains("NTNX")) {
                logger.info("trimUpdateInternetStocklist: NTNX found");
            }
            // This takes a long time. xxxxx
            prepStmt = c.prepareStatement(
                    "SELECT * FROM STOCKLISTTEMPORARY WHERE SYMBOL = ? "
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
                             logger.info ("trimUpdateInternetStocklist: Potential new stock. Will check later if exists {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
                            break;
                    case 1: // logger.info ("internetStocklist Already exists {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
                            break;
                    default:// Means found twice in STOCKLISTBLOOMBERG. Possible. It gets trimmed later.
                            logger.warn("trimUpdateInternetStocklist: Found Twice {}-{}-{} ",s.getSymbol(),s.getName(),s.getExchange());
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
     
        logger.info("trimUpdateInternetStocklist: last day data...Done");
        return true;
    }
    


    /**
     * save LastDayStockListPricetoDb 
     * Trims Stocklist and returns list containing stock symbols with valid Last Day Day
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
    
    public boolean trimStockList (List<Stock> stockList ) throws SQLException, IOException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException {
       
        List<StockPrice> stockListTrim = new ArrayList<>(); 
        
        int id1=0; // IndexService for first scan of stock symbol reading
        int id2=0; // GooglePrice pass
        int totalFetchInPass=0; // Total number of stock to fetch daily price per pass
        int totalFetch=0; // Total number of stock fetched overall
        int length;
        String symbol;      
        String exchange;
        int totalSymbol;
        
        // Holds Last day stockprice. IndexService must match stocklist Symbol since stockprice doesn't contain symbol name.
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
               
        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();
 
        for (Stock s: stockList) {    
            id1++;
           
            // Add , if not first symbol of chain
            if ((id1 >1 ) && (id1%Parameters.GOOGLEMAXSTOCKATATIME != 1)){
               symbolListSb.append(",");            
            }
            
            symbol = s.getSymbol();
/* 
            if (symbol.contains("NTNX")) {
                logger.info("trimStockList: NTNX found");
            }
*/ 
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
                        totalFetchInPass = stockPriceHistoryDao.downloadLastDayStockHistory(symbolListSb,lastDaystockPriceListPartial);
                    
//                        totalFetchInPass = downloadLastDayStockHistory(symbolListSb,lastDaystockPriceListPartial);
                    } catch (Exception ex) {
                        Logger.getLogger(StockService.class.getName()).log(Level.SEVERE, null, ex);
                        logger.error("downloadLastDayStockHistory error: ");
                        continue;
                    }
                    totalFetch =  totalFetch+totalFetchInPass;
                    ProgressBar.UpdatePosition(pbar, id1,totalSymbol,startTimeDailyHis);
                    symbolListSb.delete(0, symbolListSb.length());
                    lastDaystockPriceList.addAll(lastDaystockPriceListPartial);
                    lastDaystockPriceListPartial.clear();
                    logger.debug("Pass #{} trimStockList: Total Feched in pass {} / Total Feched so far {}  / Total Symbols {}",id2,totalFetchInPass,totalFetch,id1);                
                
            }   // if        
            if (id1 > Parameters.MAXSTOCKTOPROCESS ) break;
        } // for stocks: stocklist
        logger.debug("Total Fetched {} / Total Symbols {}",totalFetch,id1);
        ProgressBar.CloseProgressBar();
        
        // We have our trim Stock list. 
        // Save temporary table in DB with fetched data from google
        // We will use this list to delete ghost symbols/

        
        Connection c = null;
        PreparedStatement prepStmt = null;

//        SqliteDaoImpl sqlDatabase = new SqliteDaoImpl();
//        sqlDatabase.openStockDb(c);
        
        databaseService.createStocklistTrimDb();
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
           databaseService.closeDatabase();
        } 
        logger.info("TrimStockList: Total PrepStatement ... done");
        prepStmt.close();
        lastDaystockPriceList.clear(); 
        c.setAutoCommit(true);          
        databaseService.closeDatabase();
// xxxx A verifier
//        if (databaseService.deleteEmptyStockRecordsfromStocklistDb() == false) {
 //       }
        return true;
    }

    public boolean getLocalStocklist(List<Stock> stocklist) throws IOException, InterruptedException, SQLException {    
    
        StockDao stockDao = new StockDaoImpl();
        
        boolean status = false;
        // Status return total number of symbols fetched.
        status = stockDao.loadStocklistFromDb(stocklist);
        return status;
  
    }
    
    
    
}
