package com.catalk.frontend.frontend;

import java.util.Observable;

class LoginCompletedEvent {
    private String token;
    public LoginCompletedEvent(String arg) {
        this.token = arg;
    }
    public String getToken() { return this.token; }
}

public class EventNotifier extends Observable {

    public EventNotifier() {}
    public void notifyLoginCompleted(String token) {
        setChanged();
        notifyObservers(new LoginCompletedEvent(token));
    }
}
