/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.StockTechSys.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        try {
            // First create new table in database
            if (sqliteDao.createChartTable()) {
                // Table created. Take Internet CompanyListSql and save it in db.
                
                Connection c = sqliteDao.openSqlDatabase();
                
                if (chartList.size() > 0) {
                    stmt = null;
                    stmt = c.createStatement();
                    c.setAutoCommit(false);
                    
                    prepStmt = c.prepareStatement( "INSERT INTO CHART (" +
                            " SYMBOL       VARCHAR(10) NOT NULL," +
                            " DATE                   TEXT, " +
                            " OPEN                   TEXT, " +
                            " HIGH                   TEXT, " +
                            " LOW                    TEXT, " +
                            " CLOSE                  TEXT, " +
                            " VOLUME                 TEXT, " +
                            " UNADJUSTEDVOLUME       TEXT, " +
                            " CHANGE                 TEXT, " +
                            " CHANGEPERCENT          TEXT, " +
                            " VWAP                   TEXT, " +
                            " LABEL                  TEXT, " +
                            " CHANGEOVERTIME         TEXT )" +
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
                    logger.info("saveChartListToDb: ChartList saved in SqlDB for symbol {}...done",symbol);
                } else {
                    logger.error("saveChartListToDb: ChartList save FAILED in SqlDB for symbol {}",symbol);
                    sqliteDao.closeSqlDatabase(c);
                    return false;
                }
                sqliteDao.closeSqlDatabase(c);
                return true;
            } else{
                // Table was not created.
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ChartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }    
}
