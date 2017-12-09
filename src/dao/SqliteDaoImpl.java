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
     */
    
    public SqliteDaoImpl()
    {
        
    }
    /** 
     * Open SQL Stock database 
     * @version 1.0 
     * @author : dj
     * @return Connection if successful else null;
     */
    @Override
    public Connection openSqlDatabase() {
        
        Connection con;
        String databaseFileName;
        databaseFileName = DATABASEFILENAME;
        StringBuffer connectionName;

        try {
          Class.forName("org.sqlite.JDBC");
          connectionName = new StringBuffer("jdbc:sqlite:"+databaseFileName);
          con = DriverManager.getConnection(connectionName.toString());
        } catch ( ClassNotFoundException | SQLException e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          logger.debug("openSqlDatabase: FAILED opening database. No connection made. Exiting...");
          // System.exit(0);
          return null;
        } 
        logger.info("openSqlDatabase: Successful.");

        return con;
    } 

    /** 
     * Close SQL Stock database 
     * @version 1.0 
     * @author : dj
     * @param c Connection to close
     * @return true if successful.
     */
    @Override
    public boolean closeSqlDatabase (Connection c)  {
        try  {
              c.close();
        } catch ( SQLException e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          logger.debug("closeSqlDatabase: FAILED closing database. Exiting...");
        return false;
        }
        logger.info("ClosedSqlDatabase: Successful.");
    return true;
    } 

    
    /** 
     * execStatement Execute an Sql Statement. 
     * @version 1.0 
     * @author : dj
     * @param strsql
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    
    @Override
    public Boolean execStatement(String strsql) throws SQLException {
            
        Connection con = openSqlDatabase();
        if (con !=null) {
            Statement stmt = null;
            stmt = con.createStatement();

            try  {
              stmt.executeUpdate(strsql);
            } catch ( Exception e ) {
                logger.error("execStatement: returned an error on {}",strsql);
                logger.error("{} : {}",e.getClass().getName(),e.getMessage() );
                closeSqlDatabase(con);
                return false;
            }
            closeSqlDatabase(con);
            return true;
        } else {
            return null;
        }      
    }
    
    /**
     * execPrepStatement - Execute Prepared Statement and return object with data.
     * @param sql 
     * @param returnGeneratedKeys
     * @param objets
     * @return
     * @throws SQLException
     */

    @Override
    public PreparedStatement execPrepStatementRead( String sql, boolean returnGeneratedKeys, Object... objets ) throws SQLException {
        
        boolean status;
        ResultSet resultSet = null;
        Connection con = openSqlDatabase();
        // c.setAutoCommit(false);
        
        PreparedStatement preparedStatement = con.prepareStatement( sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS );
        for ( int i = 0; i < objets.length; i++ ) {
            preparedStatement.setObject( i + 1, objets[i] );
        }
        
        resultSet = preparedStatement.executeQuery();  
        // c.setAutoCommit(true);
        status = closeSqlDatabase(con);

        return preparedStatement;
    }

    
    
     /** 
     *
     * Create Stock database 
     * @version 1.0 
     * @author : dj
     * @return true if successful.
     * @throws java.sql.SQLException
     */
    @Override
    public boolean createCompanyListTable () throws SQLException { 
//        stmt = null;
//        stmt = c.createStatement();

        logger.info("createCompanylistTable starting"); 
        String query = // FOR NOW delete table if already exists
                        "DROP TABLE IF EXISTS COMPANY;" +
                        "CREATE TABLE COMPANY " +
//                        "(ID INT PRIMARY KEY NOT NULL," +
                        "( SYMBOL             VARCHAR(10) NOT NULL," +                        
                        " COMPANYNAME        TEXT NOT NULL, " +
                        " EXCHANGE           TEXT, " +
                        " INDUSTRY           TEXT, " + 
                        " WEBSITE            TEXT, " +
                        " DESCRIPTION        TEXT, " +
                        " CEO                TEXT, " +
                        " ISSUETYPE          TEXT, " +
                        " SECTOR             TEXT, " +
//                       " OBSOLETE           INTEGER,  " +
                        " UNIQUE (SYMBOL, EXCHANGE) ON CONFLICT IGNORE " +
                        ");" +
                        "CREATE UNIQUE INDEX SYMBOL_IDX ON COMPANY(SYMBOL,EXCHANGE);"
                ;
    
        if (execStatement(query) == true) {
            logger.info("createCompanylistTable created successfully"); 
        } else {
            logger.error("createCompanylistTable did not complete"); 
            return false;
        }
        return true;   
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