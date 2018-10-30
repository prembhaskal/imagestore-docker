package com.nokia.neo.imagestore;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorageService {

    void storeImage(MultipartFile file, String fileName) throws ImageStoreException;

    void storeImage(MultipartFile file, String fileName , String albumName) throws ImageStoreException;

    Resource getImage(String imageName) throws ImageStoreException;

    Resource getImage(String imageName, String albumName) throws ImageStoreException;

    List<String> getImagesInAlbum(String albumName);

    boolean deleteImage(String imageName) throws ImageStoreException;

    boolean deleteImage(String imageName, String albumName) throws ImageStoreException;

    boolean deleteAlbum(String albumName) throws ImageStoreException;

}
