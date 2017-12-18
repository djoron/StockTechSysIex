/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.StockTechSys.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import models.Chart;

/**
 *
 * @author dominicj
 */
public class ChartDaoImpl implements ChartDao {
    
    @Override
    public boolean saveChartListToDb(List<Chart> chartList, String symbol){
    
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
                logger.info("saveChartListToDb: {} days saved in SqlDB for symbol {}...",count,symbol);
            } catch ( Exception e ) {
                logger.error("saveChartListToDb: Prep Statement failed");
                logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                return false;
            }
        } else {
            logger.error("saveChartListToDb: ChartList save FAILED in SqlDB at symbol {}.",symbol);
            sqliteDao.closeSqlDatabase(c);
            return false;
        }
        sqliteDao.closeSqlDatabase(c);
        return true;  
    }    
}
