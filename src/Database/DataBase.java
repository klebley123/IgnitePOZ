package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String URL = "jdbc:sqlserver://LAPTOP-RJLJ2K5G;databaseName=IgnitePOZ;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "Haslo!23";

    public static Connection connect(){
        try{
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
