/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.StockTechSys.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Chart;
import models.Stock;

/**
 *
 * @author dominicj
 */
public class ChartDaoImpl implements ChartDao {
    
    @Override
    public int saveChartListToDb(List<Chart> chartList, String symbol){
    
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        int count = 0;
        
        Connection c = sqliteDao.openSqlDatabase();

        if (chartList.size() > 0) {
            stmt = null;
    
            try {
                stmt = c.createStatement();
                c.setAutoCommit(false);
                prepStmt = c.prepareStatement( "INSERT INTO CHART (" +
                        " SYMBOL, DATE, OPEN, HIGH, LOW, CLOSE, " +
                        " VOLUME, UNADJUSTEDVOLUME, CHANGE, CHANGEPERCENT, " +
                        " VWAP, LABEL, CHANGEOVERTIME)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");

                for (Chart ch: chartList) {
                    prepStmt.setString(1,symbol);
                    prepStmt.setString(2,ch.getDate());
                    prepStmt.setString(3,ch.getOpen());
                    prepStmt.setString(4,ch.getHigh());
                    prepStmt.setString(5,ch.getLow());
                    prepStmt.setString(6,ch.getClose());
                    prepStmt.setString(7,ch.getVolume());
                    prepStmt.setString(8,ch.getUnadjustedVolume());
                    prepStmt.setString(9,ch.getChange());
                    prepStmt.setString(10,ch.getChangePercent());
                    prepStmt.setString(11,ch.getVwap());
                    prepStmt.setString(12,ch.getLabel());
                    prepStmt.setString(13,ch.getChangeOverTime());

                    prepStmt.addBatch();
                    count++;
                }
                
                prepStmt.executeBatch();
                c.commit();
            
                prepStmt.close();
                c.setAutoCommit(true);
            } catch ( Exception e ) {
                logger.error("saveChartListToDb: Prep Statement failed");
                logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                return 0;
            }
        } else {
            logger.error("saveChartListToDb: ChartList save FAILED in SqlDB at symbol {}.",symbol);
            sqliteDao.closeSqlDatabase(c);
            return 0;
        }
        sqliteDao.closeSqlDatabase(c);
        return count;  
    }    

    public String getLastSavedDownloadChartDate(String symbol){

        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        int count = 0;
        
        Connection c = sqliteDao.openSqlDatabase();

        if (symbol != null) {
            stmt = null;
    
            try {
                stmt = c.createStatement();
                c.setAutoCommit(false);
                prepStmt = c.prepareStatement( 
                        "SELECT * FROM CHART" +
                        " WHERE SYMBOL = ?" +
                        " AND DATE = (SELECT MAX(DATE)" + 
                        " FROM CHART WHERE SYMBOL = ?)"
                );

                    prepStmt.setString(1,symbol);
                    prepStmt.setString(2,symbol);
                    prepStmt.addBatch();
                    prepStmt.executeBatch();
                    c.commit();

                    ResultSet rs = prepStmt.executeQuery();
                    int found = 0;
                    try {
                        while ( rs.next() ) {
                            // if we go in while, there is 1 element
                            found++;                
                        }

                        prepStmt.close();
                        c.setAutoCommit(true);
                    } catch ( Exception e ) {
                        logger.error("getLastSavedDownloadChartDate: Prep Statement failed");
                        logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                        return null;
                    }
            }   catch (SQLException ex) {
                    Logger.getLogger(ChartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            logger.error("getLastSavedDownloadChartDate: Null symbol passed as argument.");
            sqliteDao.closeSqlDatabase(c);
            return null;
        }
        return null;
    }
}
/*xxx

        
        
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

    
    
    
    
        
    }

}



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
*/