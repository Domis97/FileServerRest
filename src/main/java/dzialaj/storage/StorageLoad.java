package dzialaj.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import dzialaj.storage.exceptions.StorageException;
import dzialaj.storage.exceptions.StorageFileNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class StorageLoad {


    public Stream<Path> loadAll(Path rootPath,String userID) {
        try {
            return Files.walk(rootPath, 1)
                    .filter(path -> !path.equals(rootPath))
                    .map(rootPath::relativize);
        } catch (IOException e) {
            setRootLocation(userID);
            throw new StorageException("Failed to load file ", e);
        }
    }

    private Path load(String filename, Path rootPath) {
        return rootPath.resolve(filename);
    }


    public Resource loadAsResource(String filename, Path rootPath) {
        try {
            Path file = load(filename,rootPath);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }


    public void setRootLocation(String userID) {
        StorageProperties.getInstance().resetLocation(userID);
    }


}
