package com.catalk.backend;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

public class Login {
    private static boolean checkUserExist(HashMap<String, String> userInfo, Connection conn) throws SQLException {
        boolean result = false;
        String sql = String.format(
                "SELECT username FROM users WHERE username = '%1$s'",
                userInfo.get("username")
        );
        ResultSet resultSet = conn.prepareStatement(sql).executeQuery();
        while (resultSet.next()) {
            if (resultSet.getString("username") == userInfo.get("username")) {
                return true;
            }
        }
        return result;
    }

    private static boolean checkPasswordMatch(HashMap<String, String> userInfo, Connection conn) throws SQLException {
        boolean result = false;
        String hash = BCrypt.hashpw(userInfo.get("password"), BCrypt.gensalt());
        String sql = String.format(
                "SELECT COUNT(*) FROM users WHERE username = '%1$s' AND password_hash = '%2$s'",
                userInfo.get("username"),
                hash
        );
        ResultSet resultSet = conn.prepareStatement(sql).executeQuery();
        while (resultSet.next()) {
            if (resultSet.getInt("count") == 1) {
                return true;
            }
        }
        return result;
    }
    public static HashMap<String, Object> login(HashMap<String, String> userInfo, Connection conn) {
        /*
        1: create hash for password
        2: connect sql, check hash
        3: create jwt and return
         */

        //possibilities, user do not exist, password do not match,

        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            boolean isUserExist = checkUserExist(userInfo, conn);
            if (isUserExist) {
                boolean isPasswordMatch = checkPasswordMatch(userInfo, conn);
                if (isPasswordMatch) {
                    //
                } else {
                    result.put("result", 1);
                    result.put("info", "PasswordNotMatch");
                    return result;
                }
            } else {
                result.put("result", 1);
                result.put("info", "UserNotFound");
                return result;
            }
        } catch (SQLException e) {
            result.put("result", 1);
            result.put("info", "SQLExpection");
            e.printStackTrace();
            return result;
        }


        String token = Jwts.builder()
                .setSubject(userInfo.get("username"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, "D4E5F6")
                .compact();

        result.put("result", 0);
        result.put("info", token);
        return result;

    }
}
