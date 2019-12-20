package dzialaj.storage.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db_conn {


    private static Db_conn ourInstance = new Db_conn();

    private Db_conn() {
    }

    public static Db_conn getInstance() {
        return ourInstance;
    };

    private final String url = Db_credentials.getInstance().getUrl();
    private final String user = Db_credentials.getInstance().getUser();
    private final String password =  Db_credentials.getInstance().getPassword();

    Connection conn = null;


    public Connection connect() {

        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection succesfull");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;

    }

    void endConnect(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
