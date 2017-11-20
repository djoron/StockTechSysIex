/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import static StockTechSys.StockTechSys.logger;

/**
 * This class will convert Numbers in notation such as 1.3M into a long value.
 * This data usually comes from Google or Yahoo.
 * Possible inputs are
 * - = Not Available
 * M = million 10E6
 * B = Billion 10E9
 * T = Trillion 10E12
 * @author atlantis
 */
public class ConvertNumber {
    
  
 public long CreateProgressBarStockList (String numberStringFromSource)
    {
    
        long number = 0;
        long multiplier = 1;
        char c;
        
        if (numberStringFromSource.length() == 0) return 0;
        
        if (numberStringFromSource.equals("-")) {
            return 0;
        } else {
            // String is not empty or doesn't contain "-"
            String nStr = new String(numberStringFromSource);
            c = nStr.charAt(nStr.length() - 1);
            // First check if last char is a letter so we know the multiplier      
            
            switch (c) {
                case ('M'): multiplier = 1000000; break;
                case ('m'): multiplier = 1000000; break;
                case ('B'): multiplier = 1000000000; break;
                case ('b'): multiplier = 1000000000; break;
                default:
                if (Character.isDigit((c))) {
                   multiplier = 1; break; 
                } else {
                    logger.error("Unknown character in volume {}");
                    multiplier = 1; break;
                }
                
            }
            // First remove , in number such as 1,3M
            nStr = nStr.replace(",", "");
            
            //String numberOnly = "";
            //numberOnly = nStr[(nStr.length()-1)];
             
                

            return number;
    }
       
    
    
    }
}
