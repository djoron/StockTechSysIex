/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import static StockTechSys.Parameters.DATABASEFILENAME;
import static StockTechSys.StockTechSys.logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author atlantis
 */
public class SqliteDaoImpl implements SqliteDao {

//    private static Connection c = null;
    /** 
     * Don't forget to close the DB when all is said and done.
     * @version 1.0 
     * @author : dj
     * @param c Connection to initialize and open
     * @return true if successful as well as connection c
     */
    
    public SqliteDaoImpl()
    {
        
    }

    public SqliteDaoImpl(Connection c) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean openStockDb(Connection c) {
        String databaseFileName;
        databaseFileName = DATABASEFILENAME;
        Statement stmt = null;
        PreparedStatement prepStmt = null;
        StringBuffer sqlstrb;
        StringBuffer connectionName;

        try {
          Class.forName("org.sqlite.JDBC");
          connectionName = new StringBuffer("jdbc:sqlite:"+databaseFileName);
          c = DriverManager.getConnection(connectionName.toString());
        } catch ( ClassNotFoundException | SQLException e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          logger.debug("Error: FAILED opening database. No connection made. Exiting...");
          System.exit(0);
          return false;
        } 
//        connectionName.setLength(0);
//        logger.debug("Opened database successfully");
        return true;
    } 


    /** 
     * Close SQL Stock database 
     * @version 1.0 
     * @author : dj
     * @param c Connection to close
     * @return true if successful.
     */
    @Override
    public boolean closeStockDb (Connection c)  {
        try  {
              c.close();
        } catch ( SQLException e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          logger.debug("Error: FAILED closing database. Exiting...");
        return false;
        }
        logger.info("Closed StockDB successfully");
    return true;
    } 

    /*
 * Initialise la requête préparée basée sur la connexion passée en argument,
 * avec la requête SQL et les objets donnés.
 */
    @Override
    public PreparedStatement execPrepStat( String sql, boolean returnGeneratedKeys, Object... objets ) throws SQLException {
        
        boolean status;
        Connection c = null;
        ResultSet resultSet = null;
        status = openStockDb(c);
        // c.setAutoCommit(false);
        
        PreparedStatement preparedStatement = c.prepareStatement( sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );
        for ( int i = 0; i < objets.length; i++ ) {
            preparedStatement.setObject( i + 1, objets[i] );
        }
        
        resultSet = preparedStatement.executeQuery();  
        // c.setAutoCommit(true);
        status = closeStockDb(c);

        return preparedStatement;
    }

}

/* Find duplicate rows with data from 2 columns

SELECT
    symbol, name, COUNT(*)
FROM
    stocklist
GROUP BY
    symbol, name
HAVING 
    COUNT(*) > 1


delete duplicate rows keeping only one

delete   from stocklist
where    rowid not in
         (
         select  min(rowid)
         from    stocklist
         group by
                 symbol
         ,       exchange
         )
*/


/*
Search values =10 
select * from stockpricedaily where ( ( (CAST(close as double)) = 10.0) and date = "2016-03-11")

*/

/* find last date update
SELECT
    date, COUNT(*)
FROM
    stockpricedaily
GROUP BY
    date
order by date 

*/