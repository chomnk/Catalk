package com.catalk.backend;

import java.util.Scanner;
import org.glassfish.tyrus.server.Server;
import jakarta.websocket.DeploymentException;



public class WebSocketMain {
    public static void main(String[] args) {
        Server server;
        server = new Server("localhost", 8080, "/chat", null, ChatEndpoint.class);

        try {
            server.start();
            System.out.println("[SERVER]: Server is up and running.....");
            System.out.println("[SERVER]: Press 't' to terminate server.....");
            Scanner scanner = new Scanner(System.in);
            String inp = scanner.nextLine();
            scanner.close();
            if (inp.equalsIgnoreCase("t")) {
                System.out.println("[SERVER]: Server successfully terminated.....");
                server.stop();
            } else {
                System.out.println("[SERVER]: Invalid input!!!!!");
            }
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }
}