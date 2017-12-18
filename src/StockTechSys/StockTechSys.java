
package StockTechSys;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
  
    Software uses Logback
    First set properties to read logback.xml file in project directory.
   
    Data provided for free by IEX.
    https://iextrading.com/api-exhibit-a 

*/

import static StockTechSys.Parameters.DATABASEFILENAME;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Stock;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.MissingResourceException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import service.CompanyService;
import service.PriceHistoryService;
import service.SqlDatabaseService;
import static utilities.DialogWindow.AskUserInputNewDatabase;
import utilities.FileOperations;

/**
 *
 * @author atlantis
 * Main Program
 */
public class StockTechSys {
    /**
     * 
     * @param args the command line 
     * @throws java.io.IOException 
     */
 // GLOBAL CONSTANT   
    // See parameters.java for initialization of program parameters.

    public static int DAILY  = 24*60*60; // = 86400 
    public static int MIN240 =   240*60; 
    public static int MIN120 =   120*60; 
    public static int MIN60  =    60*60; 
    public static int MIN30  =    30*60; 
    public static int MIN15  =    15*60; 
    public static int MIN10  =    10*60; 
    public static int MIN5   =     5*60; 
    
    // Set Logback properties before it gets loaded.          
    static { System.setProperty("logback.configurationFile", "logback.xml");}    
    public static Logger logger = LoggerFactory.getLogger("StockTechSys Log");
    // Total year for daily Price list download history

    // Will contain Entire Stock List to/from SQLDB.
    private final static List<Stock> stockList = new ArrayList<>(); 

    // Will contain Entire StockList from the Internet
    private final static List<Stock> iexstockList = new ArrayList<>(); 
    // Will contain only new stocks to add while updating from Bloomberg an existing DB

    public static void main(String[] args) throws IOException, SQLException, MissingResourceException, InterruptedException, MalformedURLException, TimeoutException, Exception {
 
        boolean status=false;   
        boolean createNewDb = false;
        String beginTimeStamp;


        // Main Service to access Database. Will be global and 
        // passed as parameter to other services.
        
        // Initialize service for Stock list management
        CompanyService companyService = new CompanyService();
        PriceHistoryService priceHistoryService = new PriceHistoryService();
        SqlDatabaseService sqlDatabaseService = new SqlDatabaseService();
        
        // Initialize service for Stock price management
 //       PriceHistoryService priceHistoryService = new PriceHistoryService(databaseService);
                
        // Parameters from Json File will be loaded first time we call up a parameter
        
        // ** Logback Initialization **
        // Set Classpath for Logback. Bach file should be in project folder.
        Runtime.getRuntime().exec("cmd /c setClasspath");

        if (logger.isDebugEnabled())
        {
            logger.debug("Logger activated");
        }
 
        logger.info("Data provided for free by IEX: https://iextrading.com/api-exhibit-a" );

        
        FileOperations fileOperation = new FileOperations();
        beginTimeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        // If DB does not exist, create new one else ask user if he wants to 
        // delete current data
        if (fileOperation.checkFileExist(DATABASEFILENAME)) {
               logger.info("Waiting for user confirmation.");
               createNewDb = AskUserInputNewDatabase();
               if (createNewDb) { fileOperation.deleteFile(DATABASEFILENAME); }
        } else createNewDb = true;
       
//        GooglePriceHistoryDaoImpl stockPriceHistoryDao;
//        stockPriceHistoryDao = new GooglePriceHistoryDaoImpl();
        
        if (createNewDb == true) {
            logger.debug("Creating new Database. Hold on.");
            sqlDatabaseService.createSqlDb();
            // Main method to retrieve stocklist from internet and save to DB.
            companyService.createCompanyList();
            // Now get priceHistory and save into DB.            
            priceHistoryService.createQuotelist();
            
  //          status = priceHistoryService.downloadFullDailyPriceHistoryandSavetoDb(); 

        } else {    
/*
            // xxx DO A BACKUP OF DB HERE ??
            status = databaseService.initializeDatabase();
            
            // Check last Date market was opened.
            String lastdate = priceHistoryService.getLastOpenMarketDate(Parameters.SYMBOLTOCHECKLASTMARKETOPENDATE);
            logger.info("Get last date market was opened: {}.",String.valueOf(lastdate));

            // Use Bloomberg to download full Stock List from internet
            logger.debug("Download Full Stock List from Internet");
            if (stockService.getStockListFromInternet(internetStockList)) 
            {
               // Initialize StokDao object
                StockDao stockListDao = new StockDaoImpl(databaseService);

                // Now check if new stocks to add in SQL Db. Use latest internet
                // data and make a new list containing potential new symbols. 
                logger.debug("Check if new stocks from Internet to add in DB...standby.");
                status = stockService.trimUpdateInternetStocklist(internetStockList, trimstocktoadd);

                // Trim stockList here. Remove duplicates and symbols with no data
                // from google. This will save a stocklist in stocklisttrimDb.
                logger.debug("Trim StockList in memory. Remove duplicates and Symbol with no data from Google.");
                status = stockService.trimStockList(trimstocktoadd);
                status = stockListDao.deleteDuplicateFromStocklistDb();
                status = stockListDao.deleteDuplicateFromStocklistTrimDb();
                stockListDao.deleteEmptyStockRecordsfromStocklistDb();
                trimstocktoadd.clear();
                // Load stocklistfrom DB to load proper format (symbol, name, exchange)
                // saved with trimStockList
                stockListDao.loadStocklistFromTrimStocklistDb(trimstocktoadd);
                // Add these in master stocklist DB
                stockListDao.addStocklisttoDb(trimstocktoadd);
                // Stocks saved to database. Update Sector Data in DB. Will update old and new stocks.
                SectorDataDaoImpl sectorData;
                sectorData = new SectorDataDaoImpl();
                sectorData.saveSectortoList(stockSectorList);
                status=sectorData.addSectorListtoDb(stockSectorList);
                // No longer need so clear it.
                trimstocktoadd.clear();
                // Download full history from Google for new stocks added in DB.
    // NO this erases STOCKPRICEDAILY. UPDATE LATER           status = SqlDatabase.saveEntireDailyStockListPricetoDbComplete(YEAR_HISTORY_STRING, DAILY, trimblstocktoadd);
                // Ok now load stocklist which contains latest changes
            } else {
                // If could not download data, use DB from database already.

                logger.info("Could not download any data...Will use existing stocklist");
            }
            stockService.getLocalStocklist(stockList);
*/
         }
/*
            //        status = saveLastDayStockListYahooPricetoDb(stockList);
        // Double check if Daily History is complete for all stocks except for last day.
        // Last day is done in saveLastDayStockListPricetoDB
        // Could have been interrupted on the initial run since it takes 4 hours to complete.
        logger.debug("Updating current database with missing daily prices");            
        status = priceHistoryService.UpdateMissingDailyHistoryToDb(DAILY,stockList);   

        // Update only missing daily data since last program run.
//        logger.debug("Updating current database with last day daily prices");                    
        status=priceHistoryService.saveLastDayStockListPricetoDb(stockList);

        // Check for old symbols. If so, tag them as obsolete in local DB.
        priceHistoryService.CheckObsoleteSymbol(stockList);
        stockService.getLocalStocklist(stockList);
        
        // Check for stock splits here and redownload symbol history if so.
        // a refaire xxx status=databaseService.CheckForStockSplit(stockList);
  */
        // Done, close stockDB and Exit program.
        // status = SqlDatabase.closeStockDb();
        String endTimeStamp = null;
        endTimeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        
        logger.info("Work complete. Begin {}. End {}",beginTimeStamp,endTimeStamp);
        exit(0);
    }   
}

    
/*
StockService stockService = new StockServiceImpl();
DatabseService dbService = new DatabaseServiceImpl();

List<Stock> stocks = stockService.getStockList();
dbService.add(stocks);
*/
// select * from stocklist where SYMBOL LIKE '%/%'

