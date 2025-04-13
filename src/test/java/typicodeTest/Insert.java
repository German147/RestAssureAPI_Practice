package typicodeTest;

import database.DBConnection;
import service.UserService;

import java.sql.Connection;

public class Insert {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getInstance();
            UserService userService = new UserService();
            userService.fetchAndInsertUsers();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
