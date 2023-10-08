package com.catalk.frontend.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class ChatGroup {
    private String groupId;
    private String username;

    private String groupName;

    private JPanel chatPanel;

    private DefaultListModel<String> model;

    private WSClient client;

    //test purpose
    private int userId = 9;

    private Thread thread;


    //GETTER
    public JPanel getChatPanel() {
        return this.chatPanel;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void addMsg(String msg) {
        this.model.addElement(msg);
    }

    public ChatGroup(String groupId, String username, String groupName) {
        this.groupId = groupId;
        this.username = username;
        this.groupName = groupName;



        //FIRST STEP: INIT PROPERTIES
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());

        this.model = new DefaultListModel<>();

        JList<String> itemList = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(itemList);
        upperPanel.add(scrollPane);

        JTextArea textArea = new JTextArea();
        bottomPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Right Side (JButton)
        JButton button = new JButton("Send");
        bottomPanel.add(button, BorderLayout.EAST);

        mainPanel.add(upperPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.chatPanel = mainPanel;

        //SECOND STEP: add SEND button listener;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                //System.out.println(text);
                if (!text.equals("")) {
                    model.addElement(text);

                    client.sendMessage(text);
                    textArea.setText("");
                }
            }
        });


        try {
            URI address = new URI("ws://127.0.0.1:8080/chat/" + this.groupId + "/" + this.username + "/" + this.userId);
            System.out.println("ws://127.0.0.1:8080/chat/" + this.groupId + "/" + this.username + "/" + this.userId);
            this.client = new WSClient(address, this.model);

            this.thread = new Thread(() -> {
                CountDownLatch latch = new CountDownLatch(1);
                this.client.run();

                try {
                    latch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            this.thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.groupName;
    }
}
