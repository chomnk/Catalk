package com.catalk.backend;


import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

@ServerEndpoint(value = "/{groupId}/{username}/{userId}")
public class ChatEndpoint {
    private Connection createConnection() {
        Connection conn = null;
        String url = "jdbc:postgresql://localhost:5432/catalk";
        String username = "calico";
        String password = "123456";

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    private ResultSet query(String sql) {
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    private int update(String sql) {
        int result = 0;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            result = preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    //GroupID -> {username -> Session}
    private HashMap<String, HashMap<String, Session>> GroupInfo = new HashMap<>();
    private Connection conn = createConnection();

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("groupId") String groupId,
            @PathParam("username") String username,
            @PathParam("userId") String userId
    ) throws IOException {
        //register user session information
        if (GroupInfo.containsKey(groupId)) {
            GroupInfo.get(groupId).put(username, session);
        } else {
            GroupInfo.put(groupId, new HashMap<>());
            GroupInfo.get(groupId).put(username, session);
        }
    }

    @OnMessage
    public void onMessage(
            Session session,
            @PathParam("groupId") String groupId,
            @PathParam("username") String username,
            @PathParam("userId") String userId,
            String messageContent
    ) throws IOException {
        //TODO: register in database;
        String sql = String.format("INSERT INTO messages (group_id, user_id, username, message, timestamp, is_media) " +
                "VALUES (%1$s, %2$s, '%3$s', '%4$s', NOW(), false);", groupId, userId, username, messageContent);
        int insertResult = update(sql);

        broadcast(groupId, username, messageContent);
    }

    @OnClose
    public void onClose(
            Session session,
            @PathParam("groupId") String groupId,
            @PathParam("username") String username
    ) throws IOException {
        GroupInfo.get(groupId).remove(username);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private void broadcast(String groupId, String username, String content) throws IOException {
        HashMap<String, Session> groupHandle = this.GroupInfo.get(groupId);
        groupHandle.forEach((n, s) -> {
            if (!(n.equals(username))) {
                synchronized (s) {
                    try {
                        s.getBasicRemote().sendObject(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}