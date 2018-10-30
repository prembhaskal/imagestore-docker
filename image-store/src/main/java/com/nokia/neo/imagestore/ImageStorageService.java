package com.nokia.neo.imagestore;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {

    void storeImage(MultipartFile file, String fileName , String albumName) throws ImageStoreException;

    Resource getImage(String imageName, String albumName) throws ImageStoreException;

    List<String> getImagesInAlbum(String albumName);

    void deleteImage(String imageName, String albumName) throws ImageStoreException;

    void deleteAlbum(String albumName) throws ImageStoreException;

}
