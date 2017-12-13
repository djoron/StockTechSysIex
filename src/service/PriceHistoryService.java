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
import models.Quote;

/**
 *
 * @author dominicj
 */
public class PriceHistoryService {

    public boolean createQuotelist() throws Exception {
        boolean status;
    
        
        load companylist;
    // Dao to access internat Data
        IexDao iexDao = new IexDaoImpl() {};
                
        for companylist elements {
            // Will contain symbol only List from Internet.
        
            iexDao.getChartList(String company, "5y");
            save in SQL database
        }
        
        
        return false;

    }
}
