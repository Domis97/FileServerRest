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

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class RestApiController {

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


    private StorageControl storageController = new StorageControl();

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonStorageStructure StorageList(@RequestParam(value = "path", defaultValue = "") String path, @RequestParam(value = "userID") String userID) {
        System.out.println("List for user: " + userID + " in dir:" + path + "\n");
        HashMap<String, ArrayList> res = storageController.dirList(path, userID);
        return new JsonStorageStructure(userID, storageController.locationGetter("", userID),
                res.get("Directories"), res.get("Files"));
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public String createDirectory(@RequestParam(value = "folderName") String folderName, @RequestParam(value = "userID") String userID) {
        System.out.println("Create folder named: " + folderName + " for user: " + userID + "\n");
        StorageControl storageController = new StorageControl();
        if (storageController.createDir(folderName, userID))
            return "succes";
        else
            return "failure";
    }


    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<Resource> serveFiles(@RequestParam(value = "fileName") String filename, @RequestParam(value = "userID") String userID) {
        System.out.println("User named: " + userID + " want to download file:" + filename + "\n");
        StorageControl storageController = new StorageControl();
        Resource file = storageController.reqFile(filename, userID);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "userID") String userID) {
        System.out.println("User named: " + userID + " want to upload file: " + file.getOriginalFilename());
        try {
            StorageControl storageControl = new StorageControl();
            storageControl.uploadFile(file, userID);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> delete(@RequestParam("fileName") String fileName, @RequestParam(value = "userID") String userID) {
        System.out.println("User named: " + userID + " want to delete file: " + fileName);
        try {
            StorageControl storageControl = new StorageControl();
            boolean result = storageControl.deleteFile(fileName, userID);
            System.out.println("\n"+result+"\n");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}


