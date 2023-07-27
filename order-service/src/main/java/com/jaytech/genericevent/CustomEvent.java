package com.jaytech.genericevent;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class CustomEvent extends ApplicationEvent {
    private String string;

    public CustomEvent(Object source, String string) {
        super(source);
        this.string = string;
    }
}