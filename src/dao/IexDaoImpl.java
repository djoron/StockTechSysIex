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
import java.util.Iterator;
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
        int count = 0;
        String urlstr; 

        // Will contain symbol only List from Internet.
        List<Company> companyList = new ArrayList<>(); 
            
//        https://api.iextrading.com/1.0/stock/aapl/company
        
        for (Symbol symbol: symbolList ) {    

            urlstr = IEXPREFIX+"stock/"+symbol.getSymbol()+"/company";
            ObjectMapper objectMapper = new ObjectMapper();
            
            try {
                Company company = objectMapper.readValue(new URL(urlstr), new TypeReference<Company>(){});
                companyList.add(company);
                logger.info("Adding {} symbols",company.getCompanyName());
                
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }   
    
            count++;
        }
         
        return companyList;    
    }

}
