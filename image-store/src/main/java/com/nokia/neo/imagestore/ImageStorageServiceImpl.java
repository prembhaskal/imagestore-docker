package com.nokia.neo.imagestore;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Component
public class ImageStorageServiceImpl implements ImageStorageService {

    public final static String IMAGE_DIR = "d:/tmp/neotest/";
    public final static String DEFAULT_ALBUM = "";

    @Override
    public void storeImage(MultipartFile file, String imgName) throws ImageStoreException {

        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }

        if (imgName == null || imgName.isEmpty()) {
            throw new IllegalArgumentException("file name cannot be null or empty");
        }

        File imageDir = new File(IMAGE_DIR, DEFAULT_ALBUM);
        File imageFile = new File(imageDir, imgName);

        if (imageFile.exists()) {
            throw new RuntimeException("file with same name already exists: " + imgName);
        }

        try {
            System.out.println("storing file with name " + imageFile.getAbsolutePath());

            Files.copy(file.getInputStream(), imageFile.toPath());
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new ImageStoreException("error storing image " + imgName);
        }
    }

    @Override
    public void storeImage(MultipartFile file, String fileName, String albumName) {

    }

    @Override
    public Resource getImage(String imageName) throws ImageStoreException {
        if (imageName == null || imageName.isEmpty()) {
            throw new IllegalArgumentException("Image name cannot be null or empty");
        }

        File imageDir = new File(IMAGE_DIR, DEFAULT_ALBUM);
        File imageFile = new File(imageDir, imageName);

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new ImageStoreException("No such image file exists : " + imageName);
        }

        return new FileSystemResource(imageFile);
    }

    @Override
    public Resource getImage(String imageName, String albumName) {
        return null;
    }

    @Override
    public List<String> getImagesInAlbum(String albumName) {
        return null;
    }

    @Override
    public boolean deleteImage(String imageName) {
        return false;
    }

    @Override
    public boolean deleteImage(String imageName, String albumName) {
        return false;
    }

    @Override
    public boolean deleteAlbum(String albumName) {
        return false;
    }
}
