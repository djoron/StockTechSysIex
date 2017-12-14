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
import models.Company;

/**
 *
 * @author dominicj
 */
public class ChartDaoImpl implements ChartDao {
    
    @Override
    public boolean saveChartListToDb(List<Chart> chartList){
    
        
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        
        // First create new table in database        
        if (sqliteDao.createChartTable()) {
           // Table created. Take Internet CompanyListSql and save it in db.
         
           Connection c = sqliteDao.openSqlDatabase();
           
           if (chartList.size() > 0) {
            stmt = null;
            stmt = c.createStatement();
            c.setAutoCommit(false);

            prepStmt = c.prepareStatement("INSERT INTO QUOTE (SYMBOL, COMPANYNAME, "
                    + "EXCHANGE, INDUSTRY, WEBSITE, DESCRIPTION, CEO, ISSUETYPE, "
                    + "SECTOR) VALUES (?,?,?,?,?,?,?,?,?);");
            for (Company s: companyList) {
                prepStmt.setString(1,s.getSymbol());
                prepStmt.setString(2,s.getCompanyName());
                prepStmt.setString(3,s.getExchange());
                prepStmt.setString(4,s.getIndustry());
                prepStmt.setString(5,s.getWebsite());
                prepStmt.setString(6,s.getDescription());
                prepStmt.setString(7,s.getCeo());
                prepStmt.setString(8,s.getIssueType());
                prepStmt.setString(9,s.getSector());

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
            logger.info("saveCompanyList: CompanyList saved in SqlDB...done");
        } else {
            logger.error("saveCompanyList: CompanyList save FAILED in SqlDB");
            sqliteDao.closeSqlDatabase(c);
            return false;
        }
            sqliteDao.closeSqlDatabase(c);
            return true;          
        } else{
        // Table was not created.
        return false;
        }
    }
        
        
        return true; 
    }
    
}
