/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.Parameters.IEXPREFIX;
import static StockTechSys.Parameters.IEXPREFIXSYMBOLS;
import static StockTechSys.Parameters.TIMEOUT;
import static StockTechSys.StockTechSys.logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.StockGoogle;

/**
 *
 * @author dominicj
 */
public class IexDaoImpl {
    
    
    
    public boolean getSymbolList() {
     
        String urlstr = IEXPREFIX+IEXPREFIXSYMBOLS;
        try {
            URL urlDaily = new URL(urlstr);
            logger.debug("IEX Url {}",urlstr);
            URLConnection connDaily = urlDaily.openConnection();
            
            connDaily.setConnectTimeout(TIMEOUT);
            connDaily.setReadTimeout(TIMEOUT);                                      
            
            HttpURLConnection http = (HttpURLConnection)urlDaily.openConnection();
            
            
            
            int statusCode = http.getResponseCode();
            if (statusCode == 200) { // 200 means url returned something ok
                try (BufferedReader in = new BufferedReader (new InputStreamReader(urlDaily.openStream()))) {

                    int lineNumber = 0;
                    String inputLine;
                    // Will build json but remove extra chars from google
                    while ((inputLine = in.readLine()) != null) {

                        lineNumber++;
                        if (lineNumber >=3 ) {

                                sbDaily.append(inputLine);
                        }
                    }
                    in.close();
                    
                }    catch (Exception ex) {                

                    logger.error("Google Daily price (DownloadGoogleLastDayStockHistory) fetch error !");
                    Logger.getLogger("Exception: {}",GooglePriceHistory.class.getName()).log(Level.SEVERE, null, ex);
                }    
        
                ObjectMapper mapper = new ObjectMapper();
                // mapper.enable(ALLOW_COMMENTS);

                // Thanks to FasterXml Json library. One command to parse a gigantic String with Json data into myList
                myList  = Arrays.asList(mapper.readValue(sbDaily.toString(),StockGoogle[].class));
                totalFetched = myList.size();
            } else if (statusCode == 400) {
                    // if code = 400 httpserver.cc: Response Code 400 meaning no data from google for stock
                String errorStr = "DownloadGoogleLastDayStockHistory: no data from Google status {}" + statusCode + "-"+
                                  "url: "+GooglePriceCommand.toString();
                logger.warn(errorStr);
                totalFetched = 0;
                myList.clear();
            } else {
                // Code 0 
                // code 301
                // Code 400
                // Code 503 = Service unavailable
                // if code = 400 httpserver.cc: Response Code 400 meaning no data from google for stock
                String errorStr = "Google Daily price (DownloadGoogleLastDayStockHistory) fetch error else if ! status {}" + statusCode + "-"+
                                  "url: "+GooglePriceCommand.toString();
                logger.error(errorStr);
                totalFetched = 0;
                myList.clear();
                //throw new Exception(errorStr);
            }
        }catch (Exception ex) {                
                // Build error string and throw it back to the calling method

                logger.error("Google Daily price (DownloadGoogleLastDayStockHistory).  !");
                logger.error("url:{}",GooglePriceCommand.toString());
                logger.error("statusCode: {}",statusCode);
                
                Logger.getLogger("Exception: {}",GooglePriceHistory.class.getName()).log(Level.SEVERE, null, ex);
                // xxx si pas de reseau, erreur ici
                throw ex;
        } 

        String stockExchange;
        String stockSymbol;
                
        // We have a myList. Restore in it Symbol with same format 
        // as what was in Stocklist
        for (StockGoogle ml: myList) {    
           stockExchange = ml.getE();
           stockSymbol = ml.getT();
        }

        // At this point, Object Mapper created a myList with data
        lastDayStockPriceList.addAll(myList);   
        
        return totalFetched;
    }

    }
    
    public boolean getCompanyList() {
    
    }
}
