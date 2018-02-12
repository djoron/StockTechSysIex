/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.StockTechSys.logger;
import dao.SqliteDao;
import dao.SqliteDaoImpl;

/**
 *
 * @author dominicj
 */
public class SqlDatabaseService {
 
    public boolean createSqlDb() throws Exception {
    
        SqliteDao sqliteDao = new SqliteDaoImpl();
        boolean status = false;
        
        status = sqliteDao.createSymbolTables() &&
                 sqliteDao.createCompanyTables() && 
                 sqliteDao.createChartTable() &&
                 sqliteDao.createQuoteTable();
        
        
        if (status) {
            logger.info("createSqlDb: Completed successfully");
        } else {
            logger.error("createSqlDb: Failed");
        }
            
        return status;
              
    }
    
    
}
