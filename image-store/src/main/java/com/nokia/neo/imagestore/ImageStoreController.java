package com.nokia.neo.imagestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.nokia.neo.imagestore.FileBasedImageStore.DEFAULT_ALBUM;


@RestController
public class ImageStoreController {

    private final ImageStorageService imageStoreService;

    @Autowired
    public ImageStoreController(ImageStorageService imageStoreService) {
        this.imageStoreService = imageStoreService;
    }

    @GetMapping("/getImage")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@RequestParam(value = "imageName") String imageName,
                                             @RequestParam(value = "albumName", required = false, defaultValue = DEFAULT_ALBUM) String albumName)
            throws ImageStoreException {

        Resource file = imageStoreService.getImage(imageName, albumName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }



    @PostMapping("/storeImage")
    public String storeImage(@RequestParam(value = "imgfile") MultipartFile file,
                             @RequestParam(value = "imgname") String imageName,
                             @RequestParam(value = "albumName", required = false, defaultValue = DEFAULT_ALBUM) String albumName) throws ImageStoreException {
        imageStoreService.storeImage(file, imageName, albumName);
        return "File Uploaded " + imageName;
    }

    @GetMapping("/deleteImage")
    public String deleteImage(@RequestParam(value = "imageName") String imageName,
                                             @RequestParam(value = "albumName", required = false, defaultValue = DEFAULT_ALBUM) String albumName)
            throws ImageStoreException {

        imageStoreService.deleteImage(imageName, albumName);

        return "deleted image: " + imageName + " in album: " + albumName;
    }

    @GetMapping("/deleteAlbum")
    public String deleteAlbum(@RequestParam(value = "albumName", required = false, defaultValue = DEFAULT_ALBUM) String albumName)
            throws ImageStoreException {

        imageStoreService.deleteAlbum(albumName);

        return "deleted album: " + albumName;
    }

    @GetMapping("/getAlbumImages")
    public List<String> getImagesInAlbum(@RequestParam(value = "albumName", required = false, defaultValue = DEFAULT_ALBUM) String albumName) {
        return imageStoreService.getImagesInAlbum(albumName);
    }
    



}