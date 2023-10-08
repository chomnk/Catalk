package com.catalk.frontend.frontend;

import jakarta.websocket.*;

import javax.swing.*;
import java.net.URI;

//https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WSClient {

    Session userSession = null;

    DefaultListModel<String> model;

    private URI address;



    public Session run() {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.userSession = container.connectToServer(this, this.address);

            if (this.userSession != null) {
                System.out.println("Connected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.userSession;

    }
    public WSClient(URI endpointURI, DefaultListModel<String> model) {
        this.address = endpointURI;
        this.model = model;
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        //this.userSession.getBasicRemote().sendPing();
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Handle WebSocket error here
        System.err.println("WebSocket error occurred: " + throwable.getMessage());
        throwable.printStackTrace();

        // Perform error recovery or other actions as needed
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        this.model.addElement(message);
    }



    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        //System.out.println(this.);
        this.userSession.getAsyncRemote().sendText(message);

    }
}