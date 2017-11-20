/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author atlantis
 */
public interface SqliteDao {
    
    public boolean openStockDb (Connection c);
    public boolean closeStockDb (Connection c);
    public PreparedStatement execPrepStat( String sql, boolean returnGeneratedKeys, Object... objets ) throws SQLException;
}
