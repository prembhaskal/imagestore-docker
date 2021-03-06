package com.nokia.neo.events;

import org.springframework.stereotype.Component;

import java.io.IOException;

public interface EventPublisher {
    void sendEvent(StoreEvent storeEvent) throws IOException;
}
