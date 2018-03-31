/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import StockTechSys.StockTechSysIex.TypeListDownload;
import static StockTechSys.StockTechSysIex.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Company;

/**
 *
 * @author dominicj
 */
public class CompanyDaoImpl implements CompanyDao {
    
    public CompanyDaoImpl() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Save Company Downloaded from internet into DB. 
     * @param companyList
     * @param companyListSql
     * @return
     * @throws Exception
     */
    @Override
    public boolean saveCompanyList(List<Company> companyList) throws Exception{
    
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        
        Connection c = sqliteDao.openSqlDatabase();

        if (companyList.size() > 0) {
            stmt = null;
            stmt = c.createStatement();
            c.setAutoCommit(false);
            // Just insert, not replace here.
            prepStmt = c.prepareStatement("INSERT INTO COMPANY (SYMBOL, COMPANYNAME, "
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
    }
    
    /**
     * LoadCompanyListFromDb 
     *  Load Company list from SQL DB and save in CompanyList list.
     * @version 1.0
     * @author : dominicj
     * @param CompanyList
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    public List<Company> loadCompanyListFromDb () throws SQLException {

        logger.info("loadCompanyListFromDb - Loading list from DB ... Standby");

        List<Company> companyList = new ArrayList<Company>();
        
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        int count=0;
        
        Connection c = sqliteDao.openSqlDatabase();

        c.setAutoCommit(false);
        prepStmt = c.prepareStatement("SELECT * FROM COMPANY");
                       
        ResultSet rs = prepStmt.executeQuery();
        try {
            while ( rs.next() ) {
                Company company = new Company();
                company.setSymbol  (rs.getString("SYMBOL"));
                company.setCompanyName(rs.getString("COMPANYNAME"));
                company.setExchange (rs.getString("EXCHANGE"));
                company.setSector (rs.getString("SECTOR"));
                company.setIndustry (rs.getString("INDUSTRY"));
                // company.setDayLastUpdate (rs.getString("DAYLASTUPDATE"));
                companyList.add(company);
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
        logger.info("Loaded Stocklist from DB ... Done");
        sqliteDao.closeSqlDatabase(c);
        return companyList;
    }
    
    /**
     * Update Company Downloaded from internet into DB. 
     * @param companyList
     * @param companyListSql
     * @return
     * @throws Exception
     */
    @Override
    public boolean updateCompanyList(List<Company> companyList) throws Exception{
    
/*        
        https://stackoverflow.com/questions/38226340/find-the-difference-between-two-collections-in-java-8
        
        List<Book> books1 = ...;
List<Book> books2 = ...;
Set<Integer> ids = books2.stream()
        .map(Book::getId)
        .collect(Collectors.toSet());
List<Book> parentBooks = books1.stream()
        .filter(book -> !ids.contains(book.getId()))
        .collect(Collectors.toList());

*/
        SqliteDao sqliteDao = new SqliteDaoImpl();
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        

        Connection c = sqliteDao.openSqlDatabase();

        if (companyList.size() > 0) {
            stmt = null;
            stmt = c.createStatement();
            c.setAutoCommit(false);

            prepStmt = c.prepareStatement("INSERT OR REPLACE INTO COMPANY (SYMBOL, COMPANYNAME, "
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
            logger.info("updateCompanyList: CompanyList saved in SqlDB...done");
        } else {
            logger.error("updateCompanyList: CompanyList save FAILED in SqlDB");
            sqliteDao.closeSqlDatabase(c);
            return false;
        }
        sqliteDao.closeSqlDatabase(c);
        return true;          
    }

}
