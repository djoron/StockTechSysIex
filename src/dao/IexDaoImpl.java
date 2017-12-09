/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.Parameters.IEXPREFIX;
import static StockTechSys.Parameters.IEXPREFIXSYMBOLS;
import static StockTechSys.StockTechSys.logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import models.Company;
import models.Symbol;

/**
 *
 * @author dominicj
 */
public abstract class IexDaoImpl implements IexDao {
    
    /**
     *
     * @param symbolList
     * @return
     * @throws MalformedURLException
     */
    @Override
    public List<Symbol> getSymbolList() throws MalformedURLException {
     
        String urlstr = IEXPREFIX+IEXPREFIXSYMBOLS;
        int size = 0; 
        List <Symbol> symbolList = null;
        logger.debug("Launching Symbol download - IEX Url {}",urlstr);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            symbolList = objectMapper.readValue(new URL(urlstr), new TypeReference<List<Symbol>>(){});
            size = symbolList.size();      
            logger.info("Read {} symbols",size);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return symbolList;
    }

    /**
     *
     * @param symbolList Internet list with only symbol name
     * @param CompanyList Interet list with symbol and company info
     * @return
     * @throws MalformedURLException
     */
    @Override
    public List<Company> getCompanyList(List<Symbol> symbolList) throws MalformedURLException {
        String urlstr; 

        // Will contain symbol only List from Internet.
        List<Company> companyList = new ArrayList<>(); 
        int totalsize = symbolList.size();
        
//        https://api.iextrading.com/1.0/stock/aapl/company
        int count = 0;
        
        for (Symbol symbol: symbolList ) {    
            
            urlstr = IEXPREFIX+"stock/"+symbol.getSymbol()+"/company";
            ObjectMapper objectMapper = new ObjectMapper();
            Company company=null;
            
            count++;
            try {
                company = objectMapper.readValue(new URL(urlstr), new TypeReference<Company>(){});
                if (company.getCompanyName() != null) {
                   companyList.add(company);
                   logger.info("getCompanyList - Symbol {} of {}: ({})-{}",count,totalsize,company.getSymbol(),company.getCompanyName());
                } else
                {
                   logger.info("getCompanyList - Skipping: ({})-{}",company.getSymbol(),company.getCompanyName());
                   count--;
                }    
                
            } catch (IOException e) {
                logger.warn("getCompanyList - Skipping unknown symbol from API: ({})",symbol.getSymbol());
                count--;
            }   
            // if (count > 10) break;
        }
        
        if (count > 0) {
            logger.info("getCompanyList- Total symbols returned {} of {} ",count,totalsize); 
        } else
        {
            logger.error("getCompanyList - No symbol returned !"); 
        }
        return companyList;    
    }

}
