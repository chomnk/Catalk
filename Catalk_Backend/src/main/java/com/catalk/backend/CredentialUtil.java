package com.catalk.backend;

import java.sql.*;

public class CredentialUtil {
    private static Connection conn;

    public static Connection createConnection() {
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

    public static boolean closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static String findUsername() {
        return "";
    }

    public static String resetPassword() {
        return "";
    }

    public static String deleteAccount() {
        return "";
    }
}
