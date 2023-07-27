package com.jaytech.genericevent;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomEvent extends ApplicationEvent {

    private Map<String,String> eventData;

    public CustomEvent(Map<String, String> eventData) {
        super("Custom event"); // You can set any descriptive event name here
        this.eventData = eventData;
    }

    public Map<String,String> getEventData() {
        return eventData;
    }
}