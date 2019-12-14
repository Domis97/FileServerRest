package restServer;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import storage.StorageControl;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class RestApiController {


    @Configuration
    @EnableWebMvc
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**");
        }
    }


    private StorageControl storageController = new StorageControl();

    @RequestMapping("/list")
    public JsonStorageStructure StorageList(@RequestParam(value = "path", defaultValue = "") String path, @RequestParam(value = "userID") String userID) {
        System.out.println("List files for user: "+userID + " in dir:"+path +"\n");
        HashMap<String, ArrayList> res = storageController.dirList(path, userID);
        return new JsonStorageStructure(userID, storageController.locationGetter("", userID),
                res.get("Directories"), res.get("Files"));
    }

    @RequestMapping("/file")
    public ResponseEntity<Resource> serveFiles(@RequestParam(value = "filename") String filename, @RequestParam(value = "userID") String userID) {
        System.out.println("User named: "+userID +" want to download file:"+filename +"\n");
        StorageControl storageController = new StorageControl();
        Resource file = storageController.reqFile(filename, userID);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping("/directory")
    public String createDirectory(@RequestParam(value = "folderName") String folderName, @RequestParam(value = "userID") String userID) {
        System.out.println("Create folder named: "+folderName+" for user: "+userID +"\n");
        StorageControl storageController = new StorageControl();
        if (storageController.createDir(folderName, userID))
            return "succes";
        else
            return "failure";
    }



    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,@RequestParam(value = "userID") String userID
    ) {
        System.out.println("User named: "+userID+" want to upload file: "+file.getOriginalFilename());
        try {
            StorageControl storageControl = new StorageControl();
            storageControl.uploadFile(file,userID);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}


