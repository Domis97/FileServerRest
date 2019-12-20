package dzialaj.restServer;


import dzialaj.storage.StorageControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
public class RestApiController {

    private StorageControl storageControl;

    @Autowired
    public RestApiController(StorageControl storageControl) {
        this.storageControl = storageControl;
    }

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
    public ResponseEntity<?> StorageList(@RequestParam(value = "path", defaultValue = "") String path,
                                         @RequestParam(value = "idToken") String idToken) {
        return storageControl.listing(idToken, path);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseEntity<?> createDirectory(@RequestParam(value = "folderName") String folderName,
                                             @RequestParam(value = "idToken") String idToken) {
        return storageControl.createDir(folderName, idToken);
    }


    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public ResponseEntity<Resource> serveFiles(@RequestParam(value = "fileName") String filename,
                                               @RequestParam(value = "idToken") String idToken) {

        Resource file = storageControl.reqFile(filename, idToken);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }


    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "idToken") String idToken) {

        return storageControl.uploadFile(file, idToken);

    }

    @RequestMapping(value = "/file", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> delete(@RequestParam("fileName") String fileName,
                                    @RequestParam(value = "idToken") String idToken) {

        return storageControl.deleteFile(fileName, idToken);

    }


}


