package com.nokia.neo.imagestore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageStoreEventsController {

    private final ImageStoreEventService imageStoreEventService;

    @Autowired
    public ImageStoreEventsController(ImageStoreEventService imageStoreEventService) {
        this.imageStoreEventService = imageStoreEventService;
    }

    @GetMapping("/getEventDetails")
    public Integer getEventDetails(@RequestParam(value = "evtType") String evtType) throws Exception {
        return imageStoreEventService.getEventDetails(evtType);
    }
}
