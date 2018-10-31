package com.nokia.neo.imagestore;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nokia.neo.AppProperties;
import com.rabbitmq.client.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ImageStoreRMQEventService implements ImageStoreEventService, DeliverCallback, CancelCallback {

    public final static String IMAGESTORE_QUEUE = "imagestore";

    private final Gson gson = new Gson();
    private final ConcurrentHashMap<StoreEvent.EventType, AtomicInteger> eventCounter;
    private final RMQEventlListener rmqEventlListener;

    public ImageStoreRMQEventService() throws IOException, TimeoutException {
        eventCounter = new ConcurrentHashMap<>();

        rmqEventlListener = new RMQEventlListener(IMAGESTORE_QUEUE, this, this);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(rmqEventlListener);
        executorService.shutdown(); // no more submits required.
    }

    @Override
    public Integer getEventDetails(String evtType) {
        StoreEvent.EventType eventType = StoreEvent.EventType.valueOf(evtType);
        AtomicInteger atomicInteger = eventCounter.get(eventType);
		
        if (atomicInteger != null) {
            return atomicInteger.get();
        }
        else {
            return 0;
        }
    }

    @Override
    public void handle(String consumerTag) {
        System.err.println("Consumer cancelled. check server logs for details, queue name: " + IMAGESTORE_QUEUE);
    }

    @Override
    public void handle(String consumerTag, Delivery deliveryMsg) throws IOException {
        String message = new String(deliveryMsg.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received '" + message + "'");
        StoreEvent storeEvent = getStoreEvent(message);
        if (storeEvent != null) {
            updateEventCounter(storeEvent);
        }
    }

    private void updateEventCounter(StoreEvent storeEvent) {
        AtomicInteger count = eventCounter.get(storeEvent.getOperation());
        if (count == null) {
            count = new AtomicInteger(0);
            eventCounter.put(storeEvent.getOperation(), count);
        }
        count.getAndIncrement();
    }

    private StoreEvent getStoreEvent(String jsonStr) {
        try {
            return gson.fromJson(jsonStr, StoreEvent.class);
        } catch (JsonSyntaxException e) {
            System.err.println(String.format("error converting Json string: %s to StoreEvent class", jsonStr));
            return null;
        }
    }
}
