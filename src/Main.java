import Database.DataBase;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Connection conn = DataBase.connect();
        if(conn != null){
            System.out.println("Połączono");
        }
        else {
            System.out.println("cos nie tak");
        }
    }
}