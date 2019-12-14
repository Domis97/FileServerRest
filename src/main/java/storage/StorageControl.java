package storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StorageControl {
    private StorageLoad storageService = new StorageLoad();


    private Path current;
    private String rootLoc = StorageProperties.getInstance().getLocation();

    public Resource reqFile(String filename, String userID) {
        locationGetter("", userID);
        return storageService.loadAsResource(filename, current);
    }

    public boolean uploadFile(MultipartFile uploadfile,String userID) {

        try {
            locationGetter("",userID);
            String directory = current.toString();
            String filename = uploadfile.getOriginalFilename();
            String filepath = Paths.get(directory, filename).toString();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            stream.write(uploadfile.getBytes());
            stream.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean createDir(String folderName, String userID) {
        try {
            locationGetter("", userID);
            Files.createDirectories(Paths.get(current.toAbsolutePath().toString() + "\\" + folderName));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public HashMap<String, ArrayList> dirList(String location, String userID) {
        locationGetter(location, userID);
        HashMap<String, ArrayList> res = new HashMap<>();
        ArrayList<String> dirs = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();
        List<String> temp = storageService.loadAll(current,userID).map(path -> {
            if (!checkIfDirectory(path.toString())) {
                return ("f" + path.toString());
            } else {
                return ("d" + path.toString());
            }
        }).collect(Collectors.toList());
        for (String string :
                temp) {
            if (string.startsWith("f"))
                files.add(string.substring(1));
            else
                dirs.add(string.substring(1));
        }
        res.put("Directories", dirs);
        res.put("Files", files);
        return res;
    }

    private boolean checkIfDirectory(String filename) {
        Resource file = storageService.loadAsResource(filename, current);
        try {
            return file.getFile().isDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String locationGetter(String location, String userID) {
        String temp;
        String rev;
        if (location.equals("")) {
            if (StorageProperties.getInstance().getSessionLocation(userID).equals("ERROR")) {
                temp = StorageProperties.getInstance().updateSessionLocation(userID, null);
            } else {
                temp = StorageProperties.getInstance().getSessionLocation(userID);
            }
            current = Paths.get(rootLoc + temp);
            return "";
        } else if (location.contains("rev")) {
            if (StorageProperties.getInstance().getSessionLocation(userID).equals("ERROR")) {
                temp = StorageProperties.getInstance().updateSessionLocation(userID, null);
                rev = "";
            } else {
                temp = StorageProperties.getInstance().updateSessionLocation(userID, "rev");
                rev = temp;
            }
            current = Paths.get(rootLoc + temp);
            return rev;
        } else {
            if (StorageProperties.getInstance().getSessionLocation(userID).equals("ERROR")) {
                temp = StorageProperties.getInstance().updateSessionLocation(userID, null);
                rev = "";
            } else {
                rev = StorageProperties.getInstance().getSessionLocation(userID);
                temp = StorageProperties.getInstance().updateSessionLocation(userID, location);
            }
            current = Paths.get(rootLoc + temp);
            return rev;
        }
    }

    public void setCurrent(Path current) {
        this.current = current;
    }
}
