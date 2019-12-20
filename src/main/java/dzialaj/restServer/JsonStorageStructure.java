package dzialaj.restServer;

import java.util.ArrayList;

public class JsonStorageStructure {


    private ArrayList<String> dirs = new ArrayList<>();
    private ArrayList<String> files = new ArrayList<>();
    private String currentDir;
    private String userID;

    public JsonStorageStructure(String userID, String currentDir, ArrayList<String> dirs, ArrayList<String> files) {
        this.userID = userID;
        this.currentDir = currentDir;
        this.dirs = dirs;
        this.files = files;
    }

    public ArrayList<String> getDirs() {
        return dirs;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public String getCurrentDir() {
        return currentDir;
    }

    public String getUserID() {
        return userID;
    }
}
