package dzialaj.storage;

import dzialaj.storage.database.Db_api;
import dzialaj.storage.database.Db_conn;

import java.sql.Connection;
import java.util.HashMap;

public class StorageProperties {


    private static StorageProperties ourInstance = new StorageProperties();

    public static StorageProperties getInstance() {
        return ourInstance;
    }

    private String location = "C:\\Users\\Dominik\\Desktop\\Dominik\\FolderTestowy";

    private HashMap<String, String> sessions = new HashMap<>();
    Connection connection = Db_conn.getInstance().connect();

    public String getSessionLocation(String userID) {
        String location = Db_api.getInstance().function_get_user_location(userID, connection);
        if (location.equals("ERROR") || location.equals("null"))
            return "ERROR";
        else
            return location;
    }

    public String updateSessionLocation(String userID, String addToPath) {
        if (!getSessionLocation(userID).equals("ERROR")) {//user found
            if (addToPath.equals("rev")) {
                String rev = getSessionLocation(userID);
                if (rev.equals("\\")) {
                    resetLocation(userID);
                    return getSessionLocation(userID);
                }
                rev = rev.substring(0, rev.lastIndexOf("\\"));
                Db_api.getInstance().procedure_change_location(userID, rev, connection);
                return getSessionLocation(userID);

            }
            Db_api.getInstance().procedure_change_location(userID, getSessionLocation(userID) + "\\" + addToPath, connection);
            return getSessionLocation(userID);
        } else {
            Db_api.getInstance().procedure_add_user(userID, connection);
            return getSessionLocation(userID);
        }
    }

    public void resetLocation(String userID) {
        Db_api.getInstance().procedure_change_location(userID, "\\", connection);
    }

    public String getLocation() {
        return location;
    }
}