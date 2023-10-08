package com.catalk.frontend.frontend;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class App {
    private JFrame frame;

    private JFrame initFrame;

    private JFrame mainFrame;

    public App() {
        EventNotifier eventNotifier = new EventNotifier();

        // Subscribe to the login completion event
        eventNotifier.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof LoginCompletedEvent) {
                    String token = ((LoginCompletedEvent) arg).getToken();
                    StatusSwitch(token);
                }
            }
        });

        this.initFrame = new JFrame("CATALK");
        this.initFrame.setContentPane(new InitPage(eventNotifier).TestPanel);
        this.initFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.initFrame.setSize(200, 300);
        this.initFrame.setLocationRelativeTo(null);
        this.initFrame.pack();

        this.frame = this.initFrame;
        this.frame.setVisible(true);
    }

    private void StatusSwitch(String token) {
        this.initFrame.setVisible(false);

        this.mainFrame = new JFrame("CATALK");
        this.mainFrame.setContentPane(new MainPage(token).MainPanel);
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setSize(600, 600);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.pack();

        this.frame = this.mainFrame;
        this.frame.setVisible(true);
    }
}