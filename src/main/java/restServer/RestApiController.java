package restServer;


import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import storage.StorageControl;
import storage.database.Db_conn;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class RestApiController {

    Connection connection = Db_conn.getInstance().connect();
    String log;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonStorageStructure StorageList(@RequestParam(value = "path", defaultValue = "") String path, @RequestParam(value = "userID") String userID) {
        log = "List for user: " + userID + " in dir:" + path;
        System.out.println(log + "\n");
        StorageControl storageControl = new StorageControl();
        if (storageControl.isUserRegistered(userID, log, connection)) {
            HashMap<String, ArrayList> res = storageControl.dirList(path, userID);
            return new JsonStorageStructure(userID, storageControl.locationGetter("", userID),
                    res.get("Directories"), res.get("Files"));
        } else {
            return null;
        }
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String createDirectory(@RequestParam(value = "folderName") String folderName, @RequestParam(value = "userID") String userID) {
        log = "Create folder named: " + folderName + " for user: " + userID;
        System.out.println(log + "\n");
        StorageControl storageControl = new StorageControl();
        if (storageControl.isUserRegistered(userID, log, connection)) {
            if (storageControl.createDir(folderName, userID))
                return "succes";
            else
                return "failure";
        } else {
            return "failure";
        }
    }


    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<Resource> serveFiles(@RequestParam(value = "fileName") String filename, @RequestParam(value = "userID") String userID) {
        log = "User named: " + userID + " want to download file:" + filename;
        System.out.println(log + "\n");
        StorageControl storageControl = new StorageControl();
        if (storageControl.isUserRegistered(userID, log, connection)) {
            Resource file = storageControl.reqFile(filename, userID);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }


    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "userID") String userID) {
        log = "User named: " + userID + " want to upload file: " + file.getOriginalFilename();
        System.out.println(log + "\n");
        StorageControl storageControl = new StorageControl();
        if (storageControl.isUserRegistered(userID, log, connection)) {
            try {
                storageControl.uploadFile(file, userID);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> delete(@RequestParam("fileName") String fileName, @RequestParam(value = "userID") String userID) {
        log = "User named: " + userID + " want to delete file: " + fileName;
        System.out.println(log +"\n");
        StorageControl storageControl = new StorageControl();
        if (storageControl.isUserRegistered(userID, log, connection)) {
            try {
                boolean result = storageControl.deleteFile(fileName, userID);
                System.out.println("\n" + result + "\n");
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }


}


