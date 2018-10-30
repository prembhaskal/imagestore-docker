package com.nokia.neo.imagestore;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// TODO - put finer synchronization to avoid blocking 2 unrelated requests.
// TODO - add checks for valid directory (represents only single directory, no forward slashes)

@Component
public class FileBasedImageStore implements ImageStorageService {

    public final static String IMAGE_DIR = "d:/tmp/neotest/";
    public final static String DEFAULT_ALBUM = "";

    @Override
    public synchronized void storeImage(MultipartFile file, String imageName, String albumName) throws ImageStoreException {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }

        doEmptyNullChecks(imageName, albumName);

        File albumDir = new File(IMAGE_DIR, albumName);

        if (albumDir.exists() && !albumDir.isDirectory()) {
            throw new RuntimeException("Album directory cannot be created. a regular file with same name exists " + albumName);
        }

        if (!albumDir.exists()) {
            boolean albumCreated = albumDir.mkdir();
            if (albumCreated) {
                System.out.println("INFO - album created : " + albumName);
            }
            else {
                System.out.println("INFO - album not create, may be it is already present or there is some other error: " + albumName);
            }
        }

        File imageFile = new File(albumDir, imageName);

        if (imageFile.exists()) {
            throw new RuntimeException("file with same name already exists: " + imageName);
        }

        try {
            System.out.println("storing file with name " + imageFile.getAbsolutePath());

            Files.copy(file.getInputStream(), imageFile.toPath());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("error storing image " + imageName, e);
        }
    }

    @Override
    public synchronized Resource getImage(String imageName, String albumName) throws ImageStoreException {
        doEmptyNullChecks(imageName, albumName);

        File imageDir = new File(IMAGE_DIR, albumName);
        File imageFile = new File(imageDir, imageName);

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new ImageStoreException("No such image file exists : " + imageName);
        }

        return new FileSystemResource(imageFile);
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

    @Override
    public synchronized List<String> getImagesInAlbum(String albumName) {
        doAlbumNullCheck(albumName);
        List<String> imageNames = new ArrayList<>();

        File albumDir = new File(IMAGE_DIR, albumName);
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
        File albumDir = new File(IMAGE_DIR, albumName);
        File imageFile = new File(albumDir, imageName);

        try {
            Files.delete(imageFile.toPath());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("Error deleting the image: " + imageName + " in album: " + albumName, e);
        }
    }

    @Override
    public synchronized void deleteAlbum(String albumName) throws ImageStoreException {
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
                }
            }
        }

        if (albumName.equals(DEFAULT_ALBUM)) { // no need to delete default album
            return;
        }
        try {
            Files.delete(albumDir.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("Error deleting the album: " + albumName + ". Please check logs for details");
        }
    }
}
