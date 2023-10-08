package com.catalk.frontend.frontend;

import javax.swing.*;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

//https://stackoverflow.com/questions/26452903/javax-websocket-client-simple-example

public class WSMain {

    public static void main(String[] args) {
        try {

            WSClient clientEndPoint = new WSClient(new URI("ws://127.0.0.1:8080/chat/basicEndpoint"), new DefaultListModel<>());
            clientEndPoint.run();
            //clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");
            CountDownLatch latch = new CountDownLatch(1);
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

