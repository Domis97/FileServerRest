package storage;

import java.util.HashMap;

public class StorageProperties {

    /**
     * Folder location for storing files
     */

    private static StorageProperties ourInstance = new StorageProperties();

    public static StorageProperties getInstance() {
        return ourInstance;
    }

    private String location = "C:\\Users\\Dominik\\Desktop\\Dominik\\FolderTestowy";

    private HashMap<String, String> sessions = new HashMap<>();

    public String getSessionLocation(String userID) {
        return sessions.getOrDefault(userID, "ERROR");
    }

    public String updateSessionLocation(String userID, String addToPath) {
        if (sessions.containsKey(userID)) {
            if(addToPath.equals("rev")){
                String rev = StorageProperties.getInstance().getSessionLocation(userID);
                if(rev.equals("\\"))
                    return sessions.put(userID, rev);
                rev = rev.substring(0,rev.lastIndexOf("\\"));
                return sessions.put(userID, rev);
            }
            sessions.put(userID, sessions.get(userID)+"\\" + addToPath);
            return sessions.get(userID);
        } else {
            sessions.put(userID, "\\");
            return sessions.get(userID);
        }
    }

    public void resetLocation(String userID){
            sessions.put(userID,"\\");
    }

    public String getLocation() {
        return location;
    }


}