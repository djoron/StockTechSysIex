/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import StockTechSys.StockTechSysIex.TypeListDownload;
import static StockTechSys.StockTechSysIex.logger;
import dao.IexDao;
import dao.IexDaoImpl;
import dao.SymbolDao;
import dao.SymbolDaoImpl;
import java.util.List;
import models.Symbol;

/**
 * Manipulate Symbol List as retrieved by IEX
 * @author dominicj
 */
public class SymbolService {
    
    /**
     * getSymbolList 
     * Download Symbol List from Internet
     * @return
     * @throws Exception
     */
    public List<Symbol> getSymbolList() throws Exception {
        
        IexDao iexDao = new IexDaoImpl() {};
        List<Symbol> symbolList = iexDao.getSymbolList();
        logger.info("Symbol List downloaded successfully");
        return symbolList;
    }

    public void saveSymbolList(TypeListDownload typeListDownload, List<Symbol> symbolList) throws Exception {
        SymbolDao symbolDao = new SymbolDaoImpl() {};
        symbolDao.saveSymbolList(symbolList, typeListDownload);
        
    }

    
}
