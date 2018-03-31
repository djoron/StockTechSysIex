/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import static StockTechSys.StockTechSysIex.logger;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader; // http://opencsv.sourceforge.net/

import java.io.FileNotFoundException;
// import com.opencsv.CSVWriter; // http://opencsv.sourceforge.net/

import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Stock;

/**
 *
 * @author atlantis
 */
public class CsvReader {
    
    public static boolean ReadCsvFileStockSectorList (String filename, List<Stock> stockSectorList) throws IOException {    
        CSVReader reader;
        boolean success=false;
        int linenum=0;
        
        /* Expect the following format from the csv file
        "Symbol","Name","LastSale","MarketCap","IPOyear","Sector","industry","Summary Quote",
        "DDD","3D Systems Corporation","13.31","$1.49B","n/a","Technology","Computer Software: Prepackaged Software","http://www.nasdaq.com/symbol/ddd",
        */
        try {
            reader = new CSVReader(new FileReader(filename));

            String[] nextLine;
            String str;
            String str2;
            while ((nextLine = reader.readNext()) != null) {
                linenum++;

                if (linenum > 1) {
    
                    Stock stockSector = new Stock();
                    stockSector.setSymbol(nextLine[0]);
                    // Bloomberg's name list replaces ' by " " and , by nothing 
                    str = nextLine[1].replaceAll("&#39;"," "); 
                    str2 = str.replaceAll(",",""); 
                    stockSector.setName(str2);
                    stockSector.setIpo(nextLine[4].equals("n/a") ? null : nextLine[4]); 
                    stockSector.setSector(nextLine[5].equals("n/a") ? null : nextLine[5]);
                    stockSector.setIndustry(nextLine[6].equals("n/a") ? null : nextLine[6]);
                    // yyy logger.info("Sector info: {}-{}-{}-{}-{}",stockSector.getSymbol(),stockSector.getName(),stockSector.getIpo(),stockSector.getSector(),stockSector.getIndustry());
                         
                    try {
                        stockSectorList.add(stockSector);
                    } catch (UnsupportedOperationException | ClassCastException | NullPointerException | IllegalArgumentException ex ){
                        Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
                        success = false;
                        return success;
                            
                    } 
                }
                    
/*            "Symbol","Name","LastSale","MarketCap","IPOyear","Sector","industry"
                System.out.println("Symbol: [" + nextLine[0] + "] - Name: [" + nextLine[1] + "]\nMarketCap: [" + nextLine[3] + "]");
                System.out.println("IPO: [" + nextLine[4] + "]\nSector: [" + nextLine[5] + "]\nIndustry: [" + nextLine[6] + "]");
                success = true;
*/
            }
            success=true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvReader.class.getName()).log(Level.SEVERE, null, ex);
            success = false;
        }
        return success;

    }

    
    
}
