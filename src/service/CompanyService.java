/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.StockTechSys.logger;
import dao.CompanyDao;
import dao.CompanyDaoImpl;
import dao.IexDao;
import dao.IexDaoImpl;
import java.util.ArrayList;
import java.util.List;
import models.Company;
import models.Symbol;
        
/**
 *
 * @author atlantis
 */
public class CompanyService {
    private CompanyService    companyService;

/*
    public StockService(DatabaseService databaseService)
    {
        this.databaseService=databaseService;

    }
*/
    
    public CompanyService() {
    }
        
    /**
     * Creates Company list. Calls internet DAOs to do so and saves it into
     * SQLDb for persistence.
     * @return True if download and save ok. False otherwise.
     * @throws Exception
     */
    public boolean createCompanyList() throws Exception {
        boolean status;
        
        // Dao to access internat Data
        IexDao iexDao = new IexDaoImpl() {};
        
        // Will contain symbol only List from Internet.
        List<Symbol> symbolList = iexDao.getSymbolList();
        
        // Use internet to download full Symbol List to populate new DB 
        if (!symbolList.isEmpty()) 
        {
            // We have data. Build company List, download info from internet.
            List<Company> companyList = iexDao.getCompanyList(symbolList);
            
            if (!companyList.isEmpty()) 
            {
                // Save in SQL DB.
                logger.info("createCompanyList: Downloaded {} elements.",companyList.size());
                logger.info("createCompanyList: getCompanyList. Saving into DB.",companyList.size());
                CompanyDao companyDao = new CompanyDaoImpl();
                companyDao.saveCompanyList(companyList);

            } else {
                // If returns 0 no data so cannot build stocklist. Must exit
                logger.info("createStockList: getCompanyList: Could not be built.");
                return false;
            }
        } else
        {
            // If returns 0 no data so cannot build stocklist. Must exit
            logger.info("createCompanyList: Could not download any data...exciting");
            return false;
        }

        
        return true;
        
    }

        /**
     * Update Company list. Calls internet DAOs to do so and saves it into
     * SQLDb for persistence.
     * @return True if download and save ok. False otherwise.
     * @throws Exception
     */
    public boolean updateCompanyList() throws Exception {
        boolean status;
        
        // Dao to access internat Data
        IexDao iexDao = new IexDaoImpl() {};
        
        // Will contain symbol only List from Internet.
        List<Symbol> symbolList = iexDao.getSymbolList();
        
        // Use internet to download full Symbol List to populate new DB 
        if (!symbolList.isEmpty()) 
        {
            // We have data. Build company List, download info from internet.
            List<Company> companyList = iexDao.getCompanyList(symbolList);
            
            if (!companyList.isEmpty()) 
            {
                // Save in SQL DB.
                logger.info("updateCompanyList: Downloaded {} elements.",companyList.size());
                logger.info("updateCompanyList: Saving into DB.",companyList.size());
                CompanyDao companyDao = new CompanyDaoImpl();
                companyDao.updateCompanyList(companyList);

            } else {
                // If returns 0 no data so cannot build stocklist. Must exit
                logger.info("updateCompanyList: getCompanyList: Could not be built.");
                return false;
            }
        } else
        {
            // If returns 0 no data so cannot build stocklist. Must exit
            logger.info("updateCompanyList: Could not download any data...exciting");
            return false;
        }

        
        return true;
        
    }

    /**
     * Read CompanyList from local DB. 
     * @return List of Companies.
     * @throws Exception
     */
    public List<Company> getCompanyListFromDb() throws Exception {
    
        List<Company> companyList = new ArrayList<Company>();

        CompanyDao companyDao = new CompanyDaoImpl();
        companyList = companyDao.loadCompanyListFromDb();    
        return companyList;

    }
}
