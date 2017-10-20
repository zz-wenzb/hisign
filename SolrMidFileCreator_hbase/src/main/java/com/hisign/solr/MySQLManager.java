package com.hisign.solr;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class MySQLManager
{
    /// object to represent mysql linkage
    private static Connection dbConnection = null;
    
    public static boolean initDBDriver()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return false;
        } 
        return true;
    }
    
    public static boolean openDbConnection()
    {
        try
        {
            dbConnection = (Connection) DriverManager.getConnection(ModuleOptions.getConnectString());
        }
        catch (SQLException e)
        {
            System.out.println("Failed to connect database, retry...");
            e.printStackTrace();
            return openDbConnection();
        }
        return true;
    }

    public static Connection getDbConnection()
    {
        return dbConnection;
    }
    
    public static void closeDbConnection()
    {
        try
        {
            if(null != dbConnection)            
            {
                dbConnection.close();
                dbConnection = null;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void setDbConnection(Connection dbConnection)
    {
        MySQLManager.dbConnection = dbConnection;
    }
}
