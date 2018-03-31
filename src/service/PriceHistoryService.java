/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.Parameters.MAXSTOCKTOPROCESS;
import static StockTechSys.Parameters.YEAR_HISTORY_STRING;
import static StockTechSys.StockTechSysIex.logger;
import dao.ChartDao;
import dao.ChartDaoImpl;
import dao.IexDao;
import dao.IexDaoImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.Chart;
import models.Company;

/**
 *
 * @author dominicj
 */
public class PriceHistoryService {

    /**
     * Create chart (price history) list for a given symbol and saves it in 
     * local Db. Take company list from local SQL DB.
     * @return
     * @throws Exception
     */
    public boolean createChartlist() throws Exception {

        CompanyService companyService = new CompanyService();
        List<Company> companyList;
        List<Chart> chartList;
        int count=0;
        
        int daysSaved = 0;
        // Get company list from SQL DB.
        companyList = companyService.getCompanyListFromDb();
        
        IexDao iexDao = new IexDaoImpl() {};
        ChartDao chartDao = new ChartDaoImpl() {};
        int totalSymbols = companyList.size();
        
        for (Company company: companyList ) {    
            try {
                count++;
                chartList = iexDao.getDailyChartList(company.getSymbol(), YEAR_HISTORY_STRING);
                if (chartList != null) {
                    daysSaved = chartDao.saveChartListToDb(chartList, company.getSymbol());
                    if (daysSaved>0) {
                        logger.info("createChartListToDb: {} of {} - {} days saved"
                        + " in SqlDB for symbol {}...",count, totalSymbols, daysSaved, company.getSymbol());
                    } else
                    {
                        logger.warn("createChartlist - Symbol {} chart NOT saved",company.getSymbol());
                        // return false; Remove, if one symbol failed, whole thing stopped.
                    }    
                }    
            } catch (IOException e) {
                logger.error("createChartlist - Exception e {}",e);
                count--;
                return false;
            }   
        } // for
        return true;
    }
    
    /**
     * Create chart (price history) list for a given symbol and saves it in 
     * local Db. Take company list from local SQL DB.
     * @return
     * @throws Exception
     */
    public boolean updateChartlist() throws Exception {

        CompanyService companyService = new CompanyService();
        List<Company> companyList;
        List<Chart> chartList;
        int count=0;
        int daysSaved = 0;
       
        boolean status;
        // Get company list from SQL DB.
        companyList = companyService.getCompanyListFromDb();
        
        IexDao iexDao = new IexDaoImpl() {};
        ChartDao chartDao = new ChartDaoImpl() {};
        
        // Will hold sorted company list by SQL fetch date
        List <Company> companySort = new ArrayList<>();
        // Need to go through service to fetch info
        int totalSymbols = companyList.size();
        
        // Build subsets of company downloads (less than 1 month history or more).
        for (Company company: companyList ) {    
            try {
                count++;
                chartList = iexDao.getDailyChartList(company.getSymbol(), YEAR_HISTORY_STRING);
                if (chartList != null) {
                    daysSaved = chartDao.saveChartListToDb(chartList, company.getSymbol());
                    if (daysSaved > 0) {
                        logger.info("updateChartListToDb: {} of {} - {} days saved"
                        + " in SqlDB for symbol {}...",count, totalSymbols, daysSaved, company.getSymbol());

                    } else
                    {
                        logger.warn("updateChartlist - Symbol {} chart NOT saved",company.getSymbol());
                        // return false; Remove, if one symbol failed, whole thing stopped.
                    }    
                }    
            } catch (IOException e) {
                logger.error("updateChartlist - Exception e {}",e);
                count--;
                return false;
            }   
            // Used mainly for debug to process less symbols
            if (count > MAXSTOCKTOPROCESS) { 
                break;
            }
        } // for
        return true;

    }
}
