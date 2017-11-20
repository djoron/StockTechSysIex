/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import StockTechSys.Parameters;
import java.time.LocalDateTime;

/**
 * Find the last day the market was open.
 * @author atlantis
 */
public class MarketDate {
    
    public String getLastFileDate(){
        /**
        * Return the last day market data is available. This will be used to make
        * name of file to be downloaded from Bloomberg. 
        * in string format yyyymmdd
        * @author atlantis
        */
        
  /* Use todayday for now. No need to check for Saturday or Sunday yet. */
                
        LocalDateTime timenow = LocalDateTime.now();
        LocalDateTime lastMarketDate;
        
        lastMarketDate = timenow;
        // Data for for current day is available after 20h from Bloomberg.
        if (timenow.getHour() < Parameters.HOURBLOOMBERGDATAAVAIL) {
            lastMarketDate = timenow.minusDays(1);
        }
                 
        int todayday = lastMarketDate.getDayOfMonth(); 
        int todaymonth = lastMarketDate.getMonthValue();
        int todayyear = lastMarketDate.getYear();  

        String output = String.format("%04d", todayyear) + String.format("%02d", todaymonth) + String.format("%02d", todayday);
                
        return output;
    }
}