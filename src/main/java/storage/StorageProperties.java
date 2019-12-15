package storage;

import storage.database.Db_api;
import storage.database.Db_conn;

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
    Db_api db_api = new Db_api();

    public String getSessionLocation(String userID) {
        if (db_api.function_get_user_location(userID, connection).equals("ERROR") || db_api.function_get_user_location(userID, connection).equals("null"))
            return "ERROR";
        else
            return db_api.function_get_user_location(userID, connection);
    }

    public String updateSessionLocation(String userID, String addToPath) {
        if (!getSessionLocation(userID).equals("ERROR")) {//user found
            if (addToPath.equals("rev")) {
                String rev = StorageProperties.getInstance().getSessionLocation(userID);
                if (rev.equals("\\")) {
                    resetLocation(userID);
                    return getSessionLocation(userID);
                }
                rev = rev.substring(0, rev.lastIndexOf("\\"));
                db_api.procedure_change_location(userID, rev, connection);
                return getSessionLocation(userID);

            }
            db_api.procedure_change_location(userID, getSessionLocation(userID) + "\\" + addToPath, connection);
            return getSessionLocation(userID);
        } else {
            db_api.procedure_add_user(userID, connection);
            return getSessionLocation(userID);
        }
    }

    public void resetLocation(String userID) {
        db_api.procedure_change_location(userID, "\\", connection);
    }

    public String getLocation() {
        return location;
    }

//
//    public String getSessionLocation(String userID) {
//        return sessions.getOrDefault(userID, "ERROR");
//    }
//
//    public String updateSessionLocation(String userID, String addToPath) {
//        if (sessions.containsKey(userID)) {
//            if(addToPath.equals("rev")){
//                String rev = StorageProperties.getInstance().getSessionLocation(userID);
//                if(rev.equals("\\"))
//                    return sessions.put(userID, rev);
//                rev = rev.substring(0,rev.lastIndexOf("\\"));
//                return sessions.put(userID, rev);
//            }
//            sessions.put(userID, sessions.get(userID)+"\\" + addToPath);
//            return sessions.get(userID);
//        } else {
//            sessions.put(userID, "\\");
//            return sessions.get(userID);
//        }
//    }
//
//    public void resetLocation(String userID){
//            sessions.put(userID,"\\");
//    }


}