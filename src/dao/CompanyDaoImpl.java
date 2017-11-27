/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import models.Company;

/**
 *
 * @author dominicj
 */
public class CompanyDaoImpl implements CompanyDao {
    
    public CompanyDaoImpl() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public boolean saveCompanyList(List<Company> companyListSql) throws Exception{
    
        SqliteDao sqliteDao = new SqliteDaoImpl();
        
        if (sqliteDao.createCompanyListTable()) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean loadCompanyList(List<Company> companyListSql) throws Exception {
        return true;
    }
    
}
