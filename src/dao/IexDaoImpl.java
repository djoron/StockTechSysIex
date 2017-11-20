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
import java.util.List;
import models.Company;

/**
 *
 * @author dominicj
 */
public class IexDaoImpl {
    
    public int getSymbolList() throws MalformedURLException {
     
        String urlstr = IEXPREFIX+IEXPREFIXSYMBOLS;
        int size = 0; 
        logger.debug("IEX Url {}",urlstr);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Company> companyList = objectMapper.readValue(new URL(urlstr), new TypeReference<List<Company>>(){});
            size = companyList.size();      
            logger.info("Read {} companies",size);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }
}
