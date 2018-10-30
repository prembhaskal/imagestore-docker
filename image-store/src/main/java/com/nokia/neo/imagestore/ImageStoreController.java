package com.nokia.neo.imagestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
public class ImageStoreController {

    private final ImageStorageService storageService;

    @Autowired
    public ImageStoreController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/getImage")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@RequestParam(value = "imageName") String imageName) throws ImageStoreException {
        Resource file = storageService.getImage(imageName);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/storeImage")
    public String storeImage(@RequestParam(value = "imgfile") MultipartFile file, @RequestParam(value = "imgname") String imageName) throws ImageStoreException {
        storageService.storeImage(file, imageName);
        return "File Uploaded " + imageName;
    }


}