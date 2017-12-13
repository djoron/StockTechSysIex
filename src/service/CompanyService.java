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
        
    public boolean createStocklist() throws Exception {
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
                logger.info("createStockList - getCompanyList. Downloaded {} elements.",companyList.size());
                logger.info("createStockList - getCompanyList. Saving into DB.",companyList.size());
                CompanyDao companyDao = new CompanyDaoImpl();
                companyDao.saveCompanyList(companyList);

            } else {
                // If returns 0 no data so cannot build stocklist. Must exit
                logger.info("createStockList - getCompanyList: Could not be built.");

            }
        } else
        {
            // If returns 0 no data so cannot build stocklist. Must exit
            logger.info("getSymbolList: Could not download any data...exciting");
        }

        
        return false;



        
    }
    
}
