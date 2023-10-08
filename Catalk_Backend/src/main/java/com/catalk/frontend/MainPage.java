package com.catalk.frontend;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainPage {
    private JScrollPane GroupListContainer;
    private JPanel RenderArea;
    private JList GroupListCore;
    public JPanel MainPanel;

    public interface CallBackOne {
        void handle(int param1, String param2);
    }

    private ArrayList<ChatGroup> TEST_readFile() {
        BufferedReader buff;
        ArrayList<ChatGroup> result = new ArrayList<>();
        try {
            buff = new BufferedReader(new FileReader("resources/data.txt"));
            String thisLine = "";
            while ((thisLine = buff.readLine()) != null) {
                String[] eax = thisLine.split(",");
                String groupId = eax[0];
                String username = eax[1];
                String groupName = eax[2];
                System.out.println(groupId + " " + username + " " + groupName);
                ArrayList<String> messageList = new ArrayList<>(Arrays.asList(eax[3].split(";")));
                ChatGroup chatGroup = new ChatGroup(groupId, username, groupName);
                messageList.forEach(i -> chatGroup.addMsg(i));
                result.add(chatGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public MainPage() {

        GroupListCore.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ChatGroup obj = (ChatGroup) GroupListCore.getSelectedValue();
                    ((CardLayout)RenderArea.getLayout()).show(RenderArea, obj.getGroupId());
                }
            }
        });

        GroupListCore.addComponentListener(new ComponentAdapter() {
            boolean isLoad = false;
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isLoad) {
                    DefaultListModel<ChatGroup> model = new DefaultListModel<>();
                    ArrayList<ChatGroup> chatGroups = TEST_readFile();
                    chatGroups.forEach(i -> model.addElement(i));
                    GroupListCore.setModel(model);
                    SwingUtilities.invokeLater(() -> GroupListCore.repaint());








                    //add each card of chatgroup to render area, which is the main cardlayout panel
                    chatGroups.forEach(i -> RenderArea.add(i.getChatPanel(), i.getGroupId()));
                    isLoad = true;
                }
            }
        });
    }
}
