/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import static StockTechSys.Parameters.YEAR_HISTORY_STRING;
import static StockTechSys.StockTechSys.logger;
import dao.ChartDao;
import dao.ChartDaoImpl;
import dao.IexDao;
import dao.IexDaoImpl;
import java.io.IOException;
import java.util.List;
import models.Chart;
import models.Company;

/**
 *
 * @author dominicj
 */
public class PriceHistoryService {

    /**
     * Create quote (price history) list for a given symbol and saves it in 
     * local Db.
     * @return
     * @throws Exception
     */
    public boolean createQuotelist() throws Exception {

        CompanyService companyService = new CompanyService();
        List<Company> companyList;
        List<Chart> chartList;
        int count=0;
        
        boolean status;
        companyList = companyService.getCompanyListFromDb();
        
    // Dao to access internat Data
        IexDao iexDao = new IexDaoImpl() {};
        ChartDao chartDao = new ChartDaoImpl() {};
        
        for (Company company: companyList ) {    
            try {
                
                chartList = iexDao.getChartList(company.getSymbol(), YEAR_HISTORY_STRING);
                if (chartList != null) {
                    status = chartDao.saveChartListToDb(chartList, company.getSymbol());
                    if (status) {
                       logger.info("createQuotelist - Symbol {} chart saved successfully",company.getSymbol());
                       count++;
                    } else
                    {
                        logger.warn("createQuotelist - Symbol {} chart NOT saved",company.getSymbol());
                        count--;
                    }    
                }    
            } catch (IOException e) {
                logger.error("createQuotelist - Exception e {}",e);
                count--;
            }   
        } // for
        return true;

    }
}
