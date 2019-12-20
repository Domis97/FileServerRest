package dzialaj.storage;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import dzialaj.GoogleAPI.TokenVerifierAndParser;
import dzialaj.restServer.JsonStorageStructure;
import dzialaj.storage.database.Db_api;
import dzialaj.storage.database.Db_conn;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageControl {
    private StorageLoad storageService = new StorageLoad();
    private Path current;
    private String rootLoc = StorageProperties.getInstance().getLocation();

    private String getUserID(String Token) {
        GoogleIdToken.Payload payLoad = TokenVerifierAndParser.getPayload(Token);
        return payLoad.getSubject();
    }

    public Resource reqFile(String filename, String tokeN) {
        String userID = getUserID(tokeN);
        locationGetter("", userID);
        return storageService.loadAsResource(filename, current);
    }

    public ResponseEntity<JsonStorageStructure> listing(String tokeN, String path) {
        String userID = getUserID(tokeN);
        if (isUserRegistered(userID, "listing for : " + userID, Db_conn.getInstance().connect())) {
            HashMap<String, ArrayList<String>> res = dirList(path, userID);
            return new ResponseEntity<>(new JsonStorageStructure(userID, locationGetter("", userID),
                    res.get("Directories"), res.get("Files")), HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?> deleteFile(String fileName, String tokeN) {
        String userID = getUserID(tokeN);
        if (isUserRegistered(userID, "delete for : " + userID, Db_conn.getInstance().connect())) {
            locationGetter("", userID);
            File file = new File(current.toString() + "\\" + fileName);
            try {
                if (checkIfDirectory(fileName)) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.deleteQuietly(file);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?> uploadFile(MultipartFile uploadFile, String tokeN) {
        String userID = getUserID(tokeN);
        if (isUserRegistered(userID, "upload for : " + userID, Db_conn.getInstance().connect())) {
            try {
                locationGetter("", userID);
                String directory = current.toString();
                String filename = uploadFile.getOriginalFilename();
                String filepath = Paths.get(directory, filename).toString();

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(uploadFile.getBytes());
                stream.close();
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public ResponseEntity<?> createDir(String folderName, String tokeN) {
        String userID = getUserID(tokeN);
        if (isUserRegistered(userID, "listing for : " + userID, Db_conn.getInstance().connect())) {
            try {
                locationGetter("", userID);
                Files.createDirectories(Paths.get(current.toAbsolutePath().toString() + "\\" + folderName));
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    public HashMap<String, ArrayList<String>> dirList(String location, String userID) {
        locationGetter(location, userID);
        HashMap<String, ArrayList<String>> res = new HashMap<>();
        ArrayList<String> dirs = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();
        List<String> temp = storageService.loadAll(current, userID).map(path -> {
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
            return temp;
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

    public boolean isUserRegistered(String userName, String log, Connection connection) {
        try {
            if (connection.isClosed())
                connection = Db_conn.getInstance().connect();

            if (Db_api.getInstance().function_select_user(userName, connection)) {
                Db_api.getInstance().procedure_add_log(userName, log, connection);
                return true;
            } else {
                Db_api.getInstance().procedure_add_log("admin", "Failed attempt to : " + log, connection);
                System.out.println("!!! Failed to authorized user. !!!");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setCurrent(Path current) {
        this.current = current;
    }
}
