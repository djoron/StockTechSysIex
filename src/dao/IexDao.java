/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.net.MalformedURLException;
import java.util.List;
import models.Chart;
import models.Company;
import models.Quote;
import models.Symbol;

/**
 *
 * @author dominicj
 */
public interface IexDao {
 
    public List<Symbol>   getSymbolList() throws Exception;
    public List<Company>  getCompanyList(List<Symbol> symbolList) throws MalformedURLException;
    public List<Chart>    getChartList(String symbol, String period) throws MalformedURLException;
}
