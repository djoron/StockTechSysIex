/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.SQLException;
import java.util.List;
import models.Company;

/**
 *
 * @author dominicj
 */
public interface CompanyDao {

    public boolean saveCompanyList(List<Company> companyListSql) throws Exception;
    public List<Company> loadCompanyListFromDb () throws SQLException;
    public boolean updateCompanyList(List<Company> companyList) throws Exception;
    
}
