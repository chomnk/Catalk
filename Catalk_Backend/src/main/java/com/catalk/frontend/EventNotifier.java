package com.catalk.frontend;

import java.util.Observable;

class LoginCompletedEvent {
    private String msg;
    public LoginCompletedEvent(String arg) {
        this.msg = arg;
    }
}

public class EventNotifier extends Observable {

    public EventNotifier() {

    }
    public void notifyLoginCompleted() {
        setChanged();
        notifyObservers(new LoginCompletedEvent("OK"));
    }
}
