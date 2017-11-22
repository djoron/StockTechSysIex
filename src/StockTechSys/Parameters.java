/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StockTechSys;

import static StockTechSys.StockTechSys.logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Stock;

/**
 *
 * @author atlantis
 * Open InitJson.json file that contains various parameters.
 * Initializes using static init sequence
 * Used http://jsonmate.com/ to validate JSON file.
 */
public class Parameters {

    public static String PARAMETERSPATH = ".\\Properties.json";
 
    public static String DATABASEFILENAME; // Database file name 
    public static String IEXPREFIX ; // Prefix before calling IEX API 
    public static String IEXPREFIXSYMBOLS; // IEX Symbols API command
    public static int    YEAR_HISTORY_INT; // 2 How far in time Daily price download. MUST BE of INT with same length (number) as below. 
    public static String YEAR_HISTORY_STRING ; // 2Y How far in time Daily price download. MUST BE of format NUMBER+Y
    public static int    IEXMAXSTOCKATATIME; // Max IEX stock price to download simultaneously.
    
    public static int MAXSTOCKTOPROCESS; 
    public static String SYMBOLTOCHECKLASTMARKETOPENDATE; // Use this stock to confirm last day market was open
    // Number of seconds in given periods for price download
    public static int MAXDAYSBEFOREOBSOLETE; // 30 Max days without price update to make symbol obsolete and stop updating it in local DB.
    public static int STRINGBUFFERSIZE; //StringBuffer max size for SQL commands
    public static int TIMEOUT; // Timeout when sending url commands.

    
    static {
        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> paramMap= new HashMap<String, Object>();
        
        try {
            paramMap = mapper.readValue(new File(PARAMETERSPATH), new TypeReference<Map<String, Object>>(){});
        } catch (IOException ex) {
            Logger.getLogger(Parameters.class.getName()).log(Level.SEVERE, null, ex);
        }

        DATABASEFILENAME = (String)paramMap.get("databaseFileName");
        logger.info("Parameter loaded DATABASEILENAME: {}",DATABASEFILENAME);
 
        IEXPREFIX = (String)paramMap.get("iexPrefix");
        logger.info("Parameter loaded IEXPREFIX: {}",IEXPREFIX);

        IEXPREFIXSYMBOLS = (String)paramMap.get("iexPrefixSymbols");
        logger.info("Parameter loaded IEXPREFIXSYMBOLS: {}",IEXPREFIXSYMBOLS);
        
        YEAR_HISTORY_INT = Integer.parseInt((String) paramMap.get("yearHistoryInt")); 
        logger.info("Parameter loaded YEAR_HISTORY_INT: {}",YEAR_HISTORY_INT);
        
        YEAR_HISTORY_STRING = (String)paramMap.get("yearHistoryString");
        logger.info("Parameter loaded YEAR_HISTORY_STRING: {}",YEAR_HISTORY_STRING);
        
        IEXMAXSTOCKATATIME = Integer.parseInt((String)  paramMap.get("iexMaxStockAtaTime"));
        logger.info("Parameter loaded IEXMAXSTOCKATATIME : {}",IEXMAXSTOCKATATIME);
        
        MAXSTOCKTOPROCESS = Integer.parseInt((String)  paramMap.get("maxStocktoProcess"));
        logger.info("Parameter loaded MAXSTOCKTOPROCESS : {}",MAXSTOCKTOPROCESS);
        
        SYMBOLTOCHECKLASTMARKETOPENDATE = (String) paramMap.get("symbolToCheckLastMarketOpenDate");
        logger.info("Parameter loaded SYMBOLTOCHECKLASTMARKETOPENDATE : {}",SYMBOLTOCHECKLASTMARKETOPENDATE);
        
        MAXDAYSBEFOREOBSOLETE = Integer.parseInt((String) paramMap.get("maxDayBeforeObsolete"));
        logger.info("Parameter loaded MAXDAYSBEFOREOBSOLETE : {}",MAXDAYSBEFOREOBSOLETE);

        STRINGBUFFERSIZE = Integer.parseInt((String) paramMap.get("stringBufferSize"));
        logger.info("Parameter loaded STRINGBUFFERSIZE : {}",STRINGBUFFERSIZE);

        TIMEOUT = Integer.parseInt((String) paramMap.get("timeout"));
        logger.info("Parameter loaded TIMEOUT : {}",TIMEOUT);

        logger.info("Done Loading Parameters.");
        
    }
}
