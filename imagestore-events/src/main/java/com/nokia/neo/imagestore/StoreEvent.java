package com.nokia.neo.imagestore;

public class StoreEvent {

    public enum  EventType {
        IMAGE_STORE,
        IMAGE_DELETE,
        ALBUM_CREATE,
        ALBUM_DELETE,
        IMAGE_RETREIVE;
    }

    private final EventType operation;
    private final String albumName;
    private final String imageName;

    public StoreEvent(EventType operation, String imageName, String albumName) {
        if (operation == null) {
            throw new IllegalArgumentException("Operation cannot be null");
        }
        this.operation = operation;
        this.imageName = imageName;
        this.albumName = albumName;
    }

    public EventType getOperation() {
        return operation;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getImageName() {
        return imageName;
    }
}
