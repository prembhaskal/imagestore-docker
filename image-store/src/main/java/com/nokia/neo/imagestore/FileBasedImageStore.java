package com.nokia.neo.imagestore;

import com.nokia.neo.events.EventPublisher;
import com.nokia.neo.events.StoreEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.nokia.neo.events.StoreEvent.EventType.*;

// TODO - put finer synchronization to avoid blocking 2 unrelated requests.
// TODO - add checks for valid directory (represents only single directory, no forward slashes)
// TODO - add album exists check
// TODO - add file exists check
// TODO - add basic checks -- length of filename, albumname, special chars etc ?? is it needed, underlying os will throw exceptions anyway!!
// TODO - refactor, logging, enum/interface for constants,

@Component
public class FileBasedImageStore implements ImageStorageService {

    // public final static String IMAGE_DIR = "d:/tmp/neotest/";
    public final static String IMAGE_DIR = "/usr/share/neo/";
    public final static String DEFAULT_ALBUM = "";
    private final EventPublisher eventPublisher;

    @Autowired
    public FileBasedImageStore(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public synchronized void storeImage(MultipartFile file, String imageName, String albumName) throws ImageStoreException {
        doFileChecks(file);
        doEmptyNullChecks(imageName, albumName);

        System.out.println(String.format("storing image with name:%s in album:%s", imageName, albumName));

        File albumDir = new File(IMAGE_DIR, albumName);
        doAlbumExistsCheck(albumName, albumDir);

        try {
            createDirectoryIfNotPresent(albumName, albumDir);
            File imageFile = new File(albumDir, imageName);
            if (imageFile.exists()) {
                throw new RuntimeException("file with same name already exists: " + imageName);
            }

            System.out.println("storing file with name " + imageFile.getAbsolutePath());
            Files.copy(file.getInputStream(), imageFile.toPath());
            eventPublisher.sendEvent(new StoreEvent(IMAGE_STORE, imageName, albumName));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("error storing image " + imageName + " --> " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized Resource getImage(String imageName, String albumName) throws ImageStoreException {
        doEmptyNullChecks(imageName, albumName);

        System.out.println(String.format("getting image with name:%s and album:%s", imageName, albumName));

        File imageDir = new File(IMAGE_DIR, albumName);
        File imageFile = new File(imageDir, imageName);

        doImageExistsCheck(imageName, imageFile);

        try {
            FileSystemResource fileSystemResource = new FileSystemResource(imageFile);
            eventPublisher.sendEvent(new StoreEvent(IMAGE_RETREIVE, imageName, albumName));
            return fileSystemResource;
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("error retrieving image " + imageName + " --> " + e.getMessage(), e);
        }

    }

    private void doImageExistsCheck(String imageName, File imageFile) throws ImageStoreException {
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new ImageStoreException("No such image file exists : " + imageName);
        }
    }

    @Override
    public synchronized List<String> getImagesInAlbum(String albumName) {
        doAlbumNullCheck(albumName);

        System.out.println(String.format("getting images in album:%s", albumName));

        List<String> imageNames = new ArrayList<>();
        File albumDir = new File(IMAGE_DIR, albumName);
        doAlbumExistsCheck(albumName, albumDir);

        File[] imgFiles = albumDir.listFiles();
        if (imgFiles != null) {
            for (File imgFile : imgFiles) {
                if (imgFile.isFile()) {
                    imageNames.add(imgFile.getName());
                }
            }
        }

        return imageNames;
    }

    @Override
    public synchronized void deleteImage(String imageName, String albumName) throws ImageStoreException {
        doEmptyNullChecks(imageName, albumName);

        System.out.println(String.format("deleting image with name:%s from album:%s", imageName, albumName));

        File albumDir = new File(IMAGE_DIR, albumName);
        File imageFile = new File(albumDir, imageName);

        try {
            Files.delete(imageFile.toPath());
            eventPublisher.sendEvent(new StoreEvent(IMAGE_DELETE, imageName, albumName));
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("Error deleting the image: " + imageName + " in album: " + albumName, e);
        }
    }

    @Override
    public synchronized void deleteAlbum(String albumName) throws ImageStoreException {
        try {
            System.out.println(String.format("deleting album:%s", albumName));

            doAlbumNullCheck(albumName);

            File albumDir = new File(IMAGE_DIR, albumName);
            File[] imgFiles = albumDir.listFiles();
            if (imgFiles != null) {
                for (File imgFile : imgFiles) {
                    if (imgFile.isFile()) {
                        boolean deleted = imgFile.delete();
                        if (!deleted) {
                            System.err.println("Error deleting the image file: " + imgFile.getAbsolutePath());
                        }
                        else {
                            eventPublisher.sendEvent(new StoreEvent(IMAGE_DELETE, imgFile.getName(), albumName));
                        }
                    }
                }
            }

            if (albumName.equals(DEFAULT_ALBUM)) { // no need to delete default album
                return;
            }

            Files.delete(albumDir.toPath());
            eventPublisher.sendEvent(new StoreEvent(ALBUM_DELETE, null, albumName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("Error deleting the album: " + albumName + ". Please check logs for details");
        }

    }


    private void doAlbumExistsCheck(String albumName, File albumDir) {
        if (albumDir.exists() && !albumDir.isDirectory()) {
            throw new RuntimeException("Album directory cannot be created. a regular file with same name exists " + albumName);
        }
    }

    private void createDirectoryIfNotPresent(String albumName, File albumDir) throws IOException {
        if (!albumDir.exists()) {
            boolean albumCreated = albumDir.mkdir();
            if (albumCreated) {
                System.out.println("album created : " + albumName);
                eventPublisher.sendEvent(new StoreEvent(ALBUM_CREATE, null, albumName));
            }
            else {
                System.out.println("album not create, may be it is already present or there is some other error: " + albumName);
            }
        }
    }

    private void doFileChecks(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
    }

    private void doEmptyNullChecks(String imageName, String albumName) {
        doImageEmptyNullCheck(imageName);

        doAlbumNullCheck(albumName);
    }

    private void doAlbumNullCheck(String albumName) {
        if (albumName == null) {
            throw new IllegalArgumentException("album name cannot be null");
        }
    }

    private void doImageEmptyNullCheck(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            throw new IllegalArgumentException("Image name cannot be null or empty");
        }
    }
}
