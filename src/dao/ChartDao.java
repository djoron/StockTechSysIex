/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import models.Chart;
import models.Company;

/**
 *
 * @author dominicj
 */
public interface ChartDao {
    
    
    public boolean saveChartListToDb(List<Chart> chartList);
    

}
