/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import StockTechSys.StockTechSys.TypeListDownload;
import static StockTechSys.StockTechSys.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Company;
import models.Symbol;

/**
 *
 * @author dominicj
 */
public class SymbolDaoImpl implements SymbolDao {
    
    public SymbolDaoImpl() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Save Company Downloaded from internet into DB. 
     * @return
     * @throws Exception
     */
    @Override
    public boolean saveSymbolList(List<Symbol> symbolList, TypeListDownload val) throws Exception{
    
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        
        Connection c = sqliteDao.openSqlDatabase();

        if (symbolList.size() > 0) {
            stmt = null;
            stmt = c.createStatement();
            c.setAutoCommit(false);
                
            if (val == TypeListDownload.TEMPORARY) {
            prepStmt = c.prepareStatement("INSERT INTO TEMPORARYSYMBOL (SYMBOL, NAME, "
                    + "DATE, ISENABLED, TYPE, IEXID) VALUES (?,?,?,?,?,?);");
            } else {
                prepStmt = c.prepareStatement("INSERT INTO SYMBOL  (SYMBOL, NAME, "
                    + "DATE, ISENABLED, TYPE, IEXID) VALUES (?,?,?,?,?,?);");
            }
            for (Symbol s: symbolList) {
                prepStmt.setString(1,s.getSymbol());
                prepStmt.setString(2,s.getName());
                prepStmt.setString(3,s.getDate());
                prepStmt.setString(4,s.getIsEnabled());
                prepStmt.setString(5,s.getType());
                prepStmt.setString(6,s.getIexId());

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
            logger.info("saveSymbolList: SymbolList saved in SqlDB...done");
        } else {
            logger.error("saveSymbolList: SymbolList save FAILED in SqlDB");
            sqliteDao.closeSqlDatabase(c);
            return false;
        }
        sqliteDao.closeSqlDatabase(c);
        return true;          
    }
    
    /**
     * LoadCompanyListFromDb 
     *  Load Company list from SQL DB and save in CompanyList list.
     * @version 1.0
     * @author : dominicj
     * @param SymbolList
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public List<Symbol> loadSymbolListFromDb () throws SQLException {

        logger.info("loadSymbolListFromDb - Loading list from DB ... Standby");

        List<Symbol> symbolList = new ArrayList<Symbol>();
        
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        int count=0;
        
        Connection c = sqliteDao.openSqlDatabase();

        c.setAutoCommit(false);
        prepStmt = c.prepareStatement("SELECT * FROM SYMBOL");
                       
        ResultSet rs = prepStmt.executeQuery();
        try {
            while ( rs.next() ) {
                Symbol symbol = new Symbol();
                symbol.setSymbol  (rs.getString("SYMBOL"));
                symbol.setName(rs.getString("NAME"));
                // company.setDayLastUpdate (rs.getString("DAYLASTUPDATE"));
                symbolList.add(symbol);
                count++;
            }
        prepStmt.close();
        c.setAutoCommit(true);          

        } catch ( Exception e ) {
          logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
          c.close();
          return null;
          // System.exit(0);
        }
        logger.info("Loaded SymbolList from DB ... Done");
        sqliteDao.closeSqlDatabase(c);
        return symbolList;
    }

}
