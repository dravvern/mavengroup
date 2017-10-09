package cn.dravvern.base;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import cn.dravvern.util.DesUtil;

public class JdbcUtil {

    
    // private static String USERNAME = "app";
    // private static String PASSWORD = "app123";
    // private static String URL ="jdbc:oracle:thin:@192.168.1.50:1521:orcl";
     
    private static final String DRIVERCLASS = "oracle.jdbc.driver.OracleDriver";
    private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    private static String USERNAME = "cs_jk";
    private static String PASSWORD = "if0LhjiVq9g62tl2eS8lJA==";
    private static String URL ="jdbc:oracle:thin:@(DESCRIPTION = "
            + "(ADDRESS = (PROTOCOL = TCP)(HOST =134.128.38.138)(PORT = 1521)) "
            + "(ADDRESS = (PROTOCOL = TCP)(HOST =134.128.38.139)(PORT = 1521)) "
            + "(LOAD_BALANCE=yes)(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = bijs)))";
    private static String DATABASENAME;
    private static boolean readConfig = false;
    private static boolean needdecrypt = true;

    static {
        try {
            Class.forName(DRIVERCLASS).newInstance();
            
            if (readConfig) {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File("config/config.properties")));
                USERNAME = prop.getProperty("db_username");
                PASSWORD = prop.getProperty("db_password");
                URL = prop.getProperty("url");
            }
            if (needdecrypt) {
                PASSWORD = DesUtil.decrypt(PASSWORD);
            }
            int startIndex = URL.lastIndexOf(":");
            DATABASENAME = URL.substring(startIndex + 1, URL.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDatabaseName() {
        return DATABASENAME;
    }

    public static Connection getConnection() {
        Connection conn = threadLocal.get();

        if (conn == null) {
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                conn.setAutoCommit(false);
                threadLocal.set(conn);
                System.out.println("[Thread]" + Thread.currentThread().getName() + "("
                        + Thread.currentThread().getId() + ")获得链接。");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static boolean closeConnection() {
        boolean isClosed = true;
        Connection conn = threadLocal.get();
        threadLocal.set(null);
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                isClosed = false;
                e.printStackTrace();
            }
        }
        return isClosed;
    }

    public void closePStatement(PreparedStatement pStatement) {
        if (pStatement != null) {
            try {
                pStatement.close();
            } catch (SQLException e) {
                System.err.println("[Close PreparedStatement Error]" + e.getMessage());
            }
        }
    }
    
    public void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("[Close PreparedStatement Error]" + e.getMessage());
            }
        }
    }
    
    public void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.err.println("[Connection rollback Error]" + e.getMessage());
            }
        }
    }
    
    public void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                System.err.println("[Connection commit Error]" + e.getMessage());
            }
        }
    }

    public void closeConn(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("[Close Connection Error]" + e.getMessage());
        }
    }
}
