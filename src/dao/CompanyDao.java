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
public interface CompanyDao {

    public boolean saveCompanyList(List<Company> companyListSql) throws Exception;
    public boolean loadCompanyList(List<Company> companyListSql) throws Exception;
    
}
